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

    // ✅ AJOUT: Injection du FactureService pour le recalcul en cascade
    @Autowired
    @Lazy // Éviter la dépendance circulaire
    private FactureService factureService;

    /**
     * Créer un bon de livraison à partir d'un bon de sortie
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
                .build();

        // 5️⃣ Créer les lignes du bon de livraison
        double totalHT = 0.0;
        double totalTVA = 0.0;

        for (LigneBonSortieEntity ligneBonSortie : bonSortie.getLigneBonSortieEntities()) {
            LigneCommandeEntity ligneCommande = ligneBonSortie.getLigneCommande();

            Double quantiteLivree = ligneBonSortie.getQuantiteSortie();
            Double prixUnitaire = ligneCommande.getPrixUnitaire();
            Double tauxTVA = ligneCommande.getProduit().getTva().doubleValue();

            Double ligneTotalHT = prixUnitaire * quantiteLivree;
            Double ligneTotalTVA = ligneTotalHT * (tauxTVA / 100.0);
            Double ligneTotalTTC = ligneTotalHT + ligneTotalTVA;

            LigneBonLivraisonEntity ligneBonLivraison = LigneBonLivraisonEntity.builder()
                    .ligneBonSortie(ligneBonSortie)
                    .ligneCommande(ligneCommande)
                    .bonLivraison(bonLivraison)
                    .totalHT(ligneTotalHT)
                    .totalTVA(ligneTotalTVA)
                    .totalTTC(ligneTotalTTC)
                    .build();

            bonLivraison.getLignesBonLivraison().add(ligneBonLivraison);

            totalHT += ligneTotalHT;
            totalTVA += ligneTotalTVA;
        }

        // 6️⃣ Mettre à jour les totaux
        bonLivraison.setTotalHT(totalHT);
        bonLivraison.setTotalTVA(totalTVA);
        bonLivraison.setTotalTTC(totalHT + totalTVA);

        return bonLivraisonRepository.save(bonLivraison);
    }

    /**
     * ✅ MISE À JOUR: Recalculer les totaux avec cascade vers la facture
     */
    @Transactional
    public BonLivraisonEntity recalculerBonLivraison(Integer idBonSortie) {
        // 1️⃣ Récupérer le bon de livraison
        BonLivraisonEntity bonLivraison = bonLivraisonRepository.findByBonSortieId(idBonSortie)
                .orElse(null);

        if (bonLivraison == null) {
            return null;
        }

        // 2️⃣ Récupérer le bon de sortie mis à jour
        BonSortieEntity bonSortie = bonSortieRepository.findById(idBonSortie)
                .orElseThrow(() -> new RuntimeException("Bon de sortie non trouvé"));

        // 3️⃣ Recalculer les totaux
        double totalHT = 0.0;
        double totalTVA = 0.0;

        for (LigneBonLivraisonEntity ligneBonLivraison : bonLivraison.getLignesBonLivraison()) {
            LigneBonSortieEntity ligneBonSortie = ligneBonLivraison.getLigneBonSortie();
            LigneCommandeEntity ligneCommande = ligneBonLivraison.getLigneCommande();

            Double quantiteLivree = ligneBonSortie.getQuantiteSortie();
            Double prixUnitaire = ligneCommande.getPrixUnitaire();
            Double tauxTVA = ligneCommande.getProduit().getTva().doubleValue();

            Double ligneTotalHT = prixUnitaire * quantiteLivree;
            Double ligneTotalTVA = ligneTotalHT * (tauxTVA / 100.0);
            Double ligneTotalTTC = ligneTotalHT + ligneTotalTVA;

            ligneBonLivraison.setTotalHT(ligneTotalHT);
            ligneBonLivraison.setTotalTVA(ligneTotalTVA);
            ligneBonLivraison.setTotalTTC(ligneTotalTTC);

            totalHT += ligneTotalHT;
            totalTVA += ligneTotalTVA;
        }

        // 4️⃣ Mettre à jour les totaux du bon de livraison
        bonLivraison.setTotalHT(totalHT);
        bonLivraison.setTotalTVA(totalTVA);
        bonLivraison.setTotalTTC(totalHT + totalTVA);

        // 5️⃣ Sauvegarder le bon de livraison
        BonLivraisonEntity bonLivraisonSauvegarde = bonLivraisonRepository.save(bonLivraison);

        // 6️⃣ ✅ NOUVEAU: Recalculer la facture associée en cascade
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

    private String genererNumeroBonLivraison() {
        long count = bonLivraisonRepository.count();
        return String.format("BL-%d-%05d", LocalDate.now().getYear(), count + 1);
    }
}