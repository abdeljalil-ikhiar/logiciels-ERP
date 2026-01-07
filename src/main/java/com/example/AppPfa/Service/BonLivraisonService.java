package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.*;
import com.example.AppPfa.Repository.BonLivraisonRepository;
import com.example.AppPfa.Repository.BonSortieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BonLivraisonService implements BonLivraisonManager {

    @Autowired
    private BonLivraisonRepository bonLivraisonRepository;

    @Autowired
    private BonSortieRepository bonSortieRepository;

    @Autowired
    @Lazy
    private FactureService factureService;

    /**
     * ✅ Créer un bon de livraison avec remise
     */
    @Override
    @Transactional
    public BonLivraisonEntity creerBonLivraison(Integer idBonSortie) {
        // 1️⃣ Récupérer le bon de sortie
        BonSortieEntity bonSortie = bonSortieRepository.findById(idBonSortie)
                .orElseThrow(() -> new RuntimeException("Bon de sortie non trouvé avec l'ID: " + idBonSortie));

        // 2️⃣ Vérifier si un bon de livraison existe déjà
        if (bonSortie.getBonLivraison() != null) {
            throw new RuntimeException("Un bon de livraison existe déjà pour ce bon de sortie");
        }

        // 3️⃣ Générer le numéro de bon de livraison
        String numeroBonLivraison = genererNumeroBonLivraison();

        // 4️⃣ Créer le bon de livraison
        BonLivraisonEntity bonLivraison = BonLivraisonEntity.builder()
                .numeroBonLivraison(numeroBonLivraison)
                .dateLivraison(LocalDate.now())
                .bonSortie(bonSortie)
                .lignesBonLivraison(new ArrayList<>())
                .totalHT(0.0)
                .totalTVA(0.0)
                .totalTTC(0.0)
                .totalRemise(0.0)
                .build();

        // 5️⃣ Créer les lignes du bon de livraison
        double totalHT = 0.0;
        double totalTVA = 0.0;
        double totalRemise = 0.0;

        for (LigneBonSortieEntity ligneBonSortie : bonSortie.getLigneBonSortieEntities()) {
            LigneCommandeEntity ligneCommande = ligneBonSortie.getLigneCommande();

            // Récupérer les valeurs
            Double quantiteLivree = ligneBonSortie.getQuantiteSortie() != null
                    ? ligneBonSortie.getQuantiteSortie() : 0.0;
            Double prixUnitaire = ligneCommande.getPrixUnitaire() != null
                    ? ligneCommande.getPrixUnitaire() : 0.0;
            Double tauxTVA = ligneCommande.getProduit() != null && ligneCommande.getProduit().getTva() != null
                    ? ligneCommande.getProduit().getTva() : 0.0;

            // ✅ Récupérer la remise depuis la ligne de commande
            Double remisePourcentage = ligneCommande.getRemisePourcentage() != null
                    ? ligneCommande.getRemisePourcentage() : 0.0;

            // ✅ Calcul avec remise
            Double htBrut = prixUnitaire * quantiteLivree;
            Double montantRemise = htBrut * (remisePourcentage / 100.0);
            Double ligneTotalHT = htBrut - montantRemise;
            Double ligneTotalTVA = ligneTotalHT * (tauxTVA / 100.0);
            Double ligneTotalTTC = ligneTotalHT + ligneTotalTVA;

            // Créer la ligne
            LigneBonLivraisonEntity ligneBonLivraison = LigneBonLivraisonEntity.builder()
                    .ligneBonSortie(ligneBonSortie)
                    .ligneCommande(ligneCommande)
                    .bonLivraison(bonLivraison)
                    .totalHT(arrondir(ligneTotalHT))
                    .totalTVA(arrondir(ligneTotalTVA))
                    .totalTTC(arrondir(ligneTotalTTC))
                    .remiseAppliquee(remisePourcentage)
                    .montantRemise(arrondir(montantRemise))
                    .build();

            bonLivraison.getLignesBonLivraison().add(ligneBonLivraison);

            totalHT += ligneTotalHT;
            totalTVA += ligneTotalTVA;
            totalRemise += montantRemise;
        }

        // 6️⃣ Mettre à jour les totaux
        bonLivraison.setTotalHT(arrondir(totalHT));
        bonLivraison.setTotalTVA(arrondir(totalTVA));
        bonLivraison.setTotalTTC(arrondir(totalHT + totalTVA));
        bonLivraison.setTotalRemise(arrondir(totalRemise));

        return bonLivraisonRepository.save(bonLivraison);
    }

    /**
     * ✅ Recalculer un bon de livraison avec remise
     */
    @Override
    @Transactional
    public BonLivraisonEntity recalculerBonLivraison(Integer idBonSortie) {
        // 1️⃣ Récupérer le bon de livraison
        BonLivraisonEntity bonLivraison = bonLivraisonRepository.findByBonSortieId(idBonSortie)
                .orElse(null);

        if (bonLivraison == null) {
            return null;
        }

        // 2️⃣ Recalculer les totaux
        double totalHT = 0.0;
        double totalTVA = 0.0;
        double totalRemise = 0.0;

        for (LigneBonLivraisonEntity ligneBonLivraison : bonLivraison.getLignesBonLivraison()) {
            LigneBonSortieEntity ligneBonSortie = ligneBonLivraison.getLigneBonSortie();
            LigneCommandeEntity ligneCommande = ligneBonLivraison.getLigneCommande();

            // Récupérer les valeurs
            Double quantiteLivree = ligneBonSortie.getQuantiteSortie() != null
                    ? ligneBonSortie.getQuantiteSortie() : 0.0;
            Double prixUnitaire = ligneCommande.getPrixUnitaire() != null
                    ? ligneCommande.getPrixUnitaire() : 0.0;
            Double tauxTVA = ligneCommande.getProduit() != null && ligneCommande.getProduit().getTva() != null
                    ? ligneCommande.getProduit().getTva() : 0.0;

            // ✅ Récupérer la remise depuis la ligne de commande
            Double remisePourcentage = ligneCommande.getRemisePourcentage() != null
                    ? ligneCommande.getRemisePourcentage() : 0.0;

            // ✅ Calcul avec remise
            Double htBrut = prixUnitaire * quantiteLivree;
            Double montantRemise = htBrut * (remisePourcentage / 100.0);
            Double ligneTotalHT = htBrut - montantRemise;
            Double ligneTotalTVA = ligneTotalHT * (tauxTVA / 100.0);
            Double ligneTotalTTC = ligneTotalHT + ligneTotalTVA;

            // Mettre à jour la ligne
            ligneBonLivraison.setTotalHT(arrondir(ligneTotalHT));
            ligneBonLivraison.setTotalTVA(arrondir(ligneTotalTVA));
            ligneBonLivraison.setTotalTTC(arrondir(ligneTotalTTC));
            ligneBonLivraison.setRemiseAppliquee(remisePourcentage);
            ligneBonLivraison.setMontantRemise(arrondir(montantRemise));

            totalHT += ligneTotalHT;
            totalTVA += ligneTotalTVA;
            totalRemise += montantRemise;
        }

        // 3️⃣ Mettre à jour les totaux du bon de livraison
        bonLivraison.setTotalHT(arrondir(totalHT));
        bonLivraison.setTotalTVA(arrondir(totalTVA));
        bonLivraison.setTotalTTC(arrondir(totalHT + totalTVA));
        bonLivraison.setTotalRemise(arrondir(totalRemise));

        // 4️⃣ Sauvegarder
        BonLivraisonEntity bonLivraisonSauvegarde = bonLivraisonRepository.save(bonLivraison);

        // 5️⃣ Recalculer la facture associée en cascade
        factureService.recalculerFactureParBonLivraison(bonLivraison.getId());

        return bonLivraisonSauvegarde;
    }

    @Override
    @Transactional(readOnly = true)
    public BonLivraisonEntity getBonLivraison(Integer id) {
        return bonLivraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bon de livraison non trouvé avec l'ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public BonLivraisonEntity getBonLivraisonByBonSortie(Integer idBonSortie) {
        return bonLivraisonRepository.findByBonSortieId(idBonSortie)
                .orElseThrow(() -> new RuntimeException("Aucun bon de livraison trouvé pour le bon de sortie: " + idBonSortie));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BonLivraisonEntity> getBonLivraisonByCommande(Integer idCommande) {
        return bonLivraisonRepository.findByBonSortieCommandeEntityId(idCommande);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BonLivraisonEntity> getAllBonLivraison() {
        return bonLivraisonRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteBonLivraison(Integer id) {
        if (!bonLivraisonRepository.existsById(id)) {
            throw new RuntimeException("Bon de livraison non trouvé avec l'ID: " + id);
        }
        bonLivraisonRepository.deleteById(id);
    }

    /**
     * Générer le numéro du bon de livraison
     */
    private String genererNumeroBonLivraison() {
        long count = bonLivraisonRepository.count();
        return String.format("BL-%d-%05d", LocalDate.now().getYear(), count + 1);
    }

    /**
     * Arrondir à 2 décimales
     */
    private double arrondir(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }
}