package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.*;
import com.example.AppPfa.Repository.BonLivraisonRepository;
import com.example.AppPfa.Repository.LigneBonLivraisonRepository;
import com.example.AppPfa.Repository.LigneBonSortieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LigneBonLivraisonService implements LigneBonLivraisonManager {

    @Autowired
    private LigneBonLivraisonRepository ligneBonLivraisonRepository;

    @Autowired
    private BonLivraisonRepository bonLivraisonRepository;

    @Autowired
    private LigneBonSortieRepository ligneBonSortieRepository;

    @Override
    @Transactional(readOnly = true)
    public LigneBonLivraisonEntity getLigneBonLivraisonById(Integer id) {
        return ligneBonLivraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne de bon de livraison non trouvée avec l'ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LigneBonLivraisonEntity> getLigneBonLivraison() {
        return ligneBonLivraisonRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LigneBonLivraisonEntity> getLigneBonLivraisonByBonLivraison(Integer bonLivraisonId) {
        return ligneBonLivraisonRepository.findByBonLivraisonId(bonLivraisonId);
    }

    @Override
    @Transactional
    public void deleteLigneBonLivraison(Integer id) {
        LigneBonLivraisonEntity ligne = ligneBonLivraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne de bon de livraison non trouvée avec l'ID: " + id));

        Integer bonLivraisonId = ligne.getBonLivraison().getId();

        ligneBonLivraisonRepository.deleteById(id);

        recalculerTotauxBonLivraison(bonLivraisonId);
    }

    @Override
    @Transactional
    public LigneBonLivraisonEntity addLigneBonLivraison(Integer bonLivraisonId, Integer ligneBonSortieId) {
        // 1️⃣ Récupérer les entités
        BonLivraisonEntity bonLivraison = bonLivraisonRepository.findById(bonLivraisonId)
                .orElseThrow(() -> new RuntimeException("Bon de livraison non trouvé avec l'ID: " + bonLivraisonId));

        LigneBonSortieEntity ligneBonSortie = ligneBonSortieRepository.findById(ligneBonSortieId)
                .orElseThrow(() -> new RuntimeException("Ligne de bon de sortie non trouvée avec l'ID: " + ligneBonSortieId));

        // 2️⃣ Récupérer la ligne de commande
        LigneCommandeEntity ligneCommande = ligneBonSortie.getLigneCommande();
        if (ligneCommande == null) {
            throw new IllegalStateException("La ligne de bon de sortie " + ligneBonSortieId + " n'est pas liée à une ligne de commande.");
        }

        // 3️⃣ Récupérer les valeurs pour le calcul
        Double quantiteLivree = ligneBonSortie.getQuantiteSortie() != null ? ligneBonSortie.getQuantiteSortie() : 0.0;
        Double prixUnitaire = ligneCommande.getPrixUnitaire() != null ? ligneCommande.getPrixUnitaire() : 0.0;
        Double tauxTVA = ligneCommande.getProduit() != null && ligneCommande.getProduit().getTva() != null
                ? ligneCommande.getProduit().getTva() : 0.0;

        // ✅ Récupérer la remise depuis la ligne de commande
        Double remisePourcentage = ligneCommande.getRemisePourcentage() != null
                ? ligneCommande.getRemisePourcentage() : 0.0;

        // 4️⃣ Calcul avec remise
        double htBrut = quantiteLivree * prixUnitaire;
        double montantRemise = htBrut * (remisePourcentage / 100.0);
        double totalHT = htBrut - montantRemise;
        double totalTVA = totalHT * (tauxTVA / 100.0);
        double totalTTC = totalHT + totalTVA;

        // 5️⃣ Créer la ligne
        LigneBonLivraisonEntity nouvelleLigne = LigneBonLivraisonEntity.builder()
                .bonLivraison(bonLivraison)
                .ligneBonSortie(ligneBonSortie)
                .ligneCommande(ligneCommande)
                .totalHT(arrondir(totalHT))
                .totalTVA(arrondir(totalTVA))
                .totalTTC(arrondir(totalTTC))
                .remiseAppliquee(remisePourcentage)
                .montantRemise(arrondir(montantRemise))
                .build();

        // 6️⃣ Enregistrer
        LigneBonLivraisonEntity ligneEnregistree = ligneBonLivraisonRepository.save(nouvelleLigne);

        // 7️⃣ Recalculer les totaux du bon de livraison
        recalculerTotauxBonLivraison(bonLivraisonId);

        return ligneEnregistree;
    }

    /**
     * Recalculer les totaux du bon de livraison
     */
    private void recalculerTotauxBonLivraison(Integer bonLivraisonId) {
        BonLivraisonEntity bonLivraison = bonLivraisonRepository.findById(bonLivraisonId)
                .orElseThrow(() -> new RuntimeException("Bon de livraison non trouvé avec l'ID: " + bonLivraisonId));

        List<LigneBonLivraisonEntity> lignes = ligneBonLivraisonRepository.findByBonLivraisonId(bonLivraisonId);

        double totalHT = 0.0;
        double totalTVA = 0.0;
        double totalRemise = 0.0;

        for (LigneBonLivraisonEntity ligne : lignes) {
            totalHT += ligne.getTotalHT() != null ? ligne.getTotalHT() : 0.0;
            totalTVA += ligne.getTotalTVA() != null ? ligne.getTotalTVA() : 0.0;
            totalRemise += ligne.getMontantRemise() != null ? ligne.getMontantRemise() : 0.0;
        }

        bonLivraison.setTotalHT(arrondir(totalHT));
        bonLivraison.setTotalTVA(arrondir(totalTVA));
        bonLivraison.setTotalTTC(arrondir(totalHT + totalTVA));
        bonLivraison.setTotalRemise(arrondir(totalRemise));

        bonLivraisonRepository.save(bonLivraison);
    }

    /**
     * Arrondir à 2 décimales
     */
    private double arrondir(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }
}