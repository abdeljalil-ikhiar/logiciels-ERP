package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.*;
import com.example.AppPfa.Repository.BonLivraisonRepository;
import com.example.AppPfa.Repository.FactureRepository;
import com.example.AppPfa.Repository.LigneFactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FactureService implements FactureManager {

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private BonLivraisonRepository bonLivraisonRepository;

    @Autowired
    private LigneFactureRepository ligneFactureRepository;

    /**
     * ✅ Créer une facture avec remise
     */
    @Override
    public FactureEntity addFacture(FactureEntity factureEntity) {
        // 1️⃣ Vérifier que le bon de livraison existe
        BonLivraisonEntity bonLivraison = bonLivraisonRepository.findById(
                factureEntity.getBonLivraison().getId()
        ).orElseThrow(() -> new RuntimeException(
                "Bon de livraison introuvable avec l'ID: " + factureEntity.getBonLivraison().getId()
        ));

        // 2️⃣ Vérifier qu'une facture n'existe pas déjà
        if (factureRepository.existsByBonLivraison_Id(bonLivraison.getId())) {
            throw new RuntimeException(
                    "Une facture existe déjà pour le bon de livraison: " + bonLivraison.getNumeroBonLivraison()
            );
        }

        // 3️⃣ Générer le numéro de facture si non fourni
        if (factureEntity.getNumeroFacture() == null || factureEntity.getNumeroFacture().isEmpty()) {
            factureEntity.setNumeroFacture(genererNumeroFacture());
        }

        // 4️⃣ Définir la date de facture si non fournie
        if (factureEntity.getDateFacture() == null) {
            factureEntity.setDateFacture(LocalDate.now());
        }

        // 5️⃣ Associer le bon de livraison
        factureEntity.setBonLivraison(bonLivraison);

        // 6️⃣ Initialiser la liste des lignes si null
        if (factureEntity.getLignesFacture() == null) {
            factureEntity.setLignesFacture(new ArrayList<>());
        }

        // 7️⃣ Créer les lignes de facture avec remise
        double totalHT = 0.0;
        double totalTVA = 0.0;
        double totalRemise = 0.0;

        for (LigneBonLivraisonEntity ligneBL : bonLivraison.getLignesBonLivraison()) {
            // ✅ Récupérer les valeurs avec remise
            Double ligneHT = ligneBL.getTotalHT() != null ? ligneBL.getTotalHT() : 0.0;
            Double ligneTVA = ligneBL.getTotalTVA() != null ? ligneBL.getTotalTVA() : 0.0;
            Double ligneTTC = ligneBL.getTotalTTC() != null ? ligneBL.getTotalTTC() : 0.0;
            Double remiseAppliquee = ligneBL.getRemiseAppliquee() != null ? ligneBL.getRemiseAppliquee() : 0.0;
            Double montantRemise = ligneBL.getMontantRemise() != null ? ligneBL.getMontantRemise() : 0.0;

            LigneFactureEntity ligneFacture = LigneFactureEntity.builder()
                    .facture(factureEntity)
                    .ligneBonLivraison(ligneBL)
                    .ligneCommande(ligneBL.getLigneCommande())
                    .totalHT(arrondir(ligneHT))
                    .totalTVA(arrondir(ligneTVA))
                    .totalTTC(arrondir(ligneTTC))
                    .remiseAppliquee(remiseAppliquee)
                    .montantRemise(arrondir(montantRemise))
                    .build();

            factureEntity.getLignesFacture().add(ligneFacture);

            totalHT += ligneHT;
            totalTVA += ligneTVA;
            totalRemise += montantRemise;
        }

        // 8️⃣ Mettre à jour les totaux
        factureEntity.setTotalHT(arrondir(totalHT));
        factureEntity.setTotalTVA(arrondir(totalTVA));
        factureEntity.setTotalTTC(arrondir(totalHT + totalTVA));
        factureEntity.setTotalRemise(arrondir(totalRemise));

        return factureRepository.save(factureEntity);
    }

    /**
     * ✅ Recalculer la facture à partir du bon de livraison avec remise
     */
    @Transactional
    public FactureEntity recalculerFactureParBonLivraison(Integer bonLivraisonId) {
        // 1️⃣ Chercher si une facture existe pour ce bon de livraison
        Optional<FactureEntity> factureOpt = factureRepository.findByBonLivraison_Id(bonLivraisonId);

        if (factureOpt.isEmpty()) {
            return null;
        }

        FactureEntity facture = factureOpt.get();

        // 2️⃣ Récupérer le bon de livraison mis à jour
        BonLivraisonEntity bonLivraison = bonLivraisonRepository.findById(bonLivraisonId)
                .orElseThrow(() -> new RuntimeException("Bon de livraison introuvable"));

        // 3️⃣ Supprimer les anciennes lignes de facture
        ligneFactureRepository.deleteByFactureId(facture.getId());
        facture.getLignesFacture().clear();

        // 4️⃣ Recréer les lignes avec remise
        double totalHT = 0.0;
        double totalTVA = 0.0;
        double totalRemise = 0.0;

        for (LigneBonLivraisonEntity ligneBL : bonLivraison.getLignesBonLivraison()) {
            Double ligneHT = ligneBL.getTotalHT() != null ? ligneBL.getTotalHT() : 0.0;
            Double ligneTVA = ligneBL.getTotalTVA() != null ? ligneBL.getTotalTVA() : 0.0;
            Double ligneTTC = ligneBL.getTotalTTC() != null ? ligneBL.getTotalTTC() : 0.0;
            Double remiseAppliquee = ligneBL.getRemiseAppliquee() != null ? ligneBL.getRemiseAppliquee() : 0.0;
            Double montantRemise = ligneBL.getMontantRemise() != null ? ligneBL.getMontantRemise() : 0.0;

            LigneFactureEntity ligneFacture = LigneFactureEntity.builder()
                    .facture(facture)
                    .ligneBonLivraison(ligneBL)
                    .ligneCommande(ligneBL.getLigneCommande())
                    .totalHT(arrondir(ligneHT))
                    .totalTVA(arrondir(ligneTVA))
                    .totalTTC(arrondir(ligneTTC))
                    .remiseAppliquee(remiseAppliquee)
                    .montantRemise(arrondir(montantRemise))
                    .build();

            facture.getLignesFacture().add(ligneFacture);

            totalHT += ligneHT;
            totalTVA += ligneTVA;
            totalRemise += montantRemise;
        }

        // 5️⃣ Mettre à jour les totaux de la facture
        facture.setTotalHT(arrondir(totalHT));
        facture.setTotalTVA(arrondir(totalTVA));
        facture.setTotalTTC(arrondir(totalHT + totalTVA));
        facture.setTotalRemise(arrondir(totalRemise));

        return factureRepository.save(facture);
    }

    @Transactional
    public FactureEntity recalculerFacture(Integer factureId) {
        FactureEntity facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture introuvable avec ID: " + factureId));

        return recalculerFactureParBonLivraison(facture.getBonLivraison().getId());
    }

    @Override
    public FactureEntity updateFactue(int id, FactureEntity factureEntity) {
        FactureEntity factureExistante = factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture introuvable avec l'ID: " + id));

        if (factureEntity.getNumeroFacture() != null) {
            factureExistante.setNumeroFacture(factureEntity.getNumeroFacture());
        }
        if (factureEntity.getDateFacture() != null) {
            factureExistante.setDateFacture(factureEntity.getDateFacture());
        }

        if (factureEntity.getLignesFacture() != null && !factureEntity.getLignesFacture().isEmpty()) {
            factureExistante.getLignesFacture().clear();
            factureEntity.getLignesFacture().forEach(ligne -> {
                ligne.setFacture(factureExistante);
                factureExistante.getLignesFacture().add(ligne);
            });
        }

        calculerTotaux(factureExistante);

        return factureRepository.save(factureExistante);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FactureEntity> getAllFacture() {
        return factureRepository.findAll();
    }

    @Override
    public void deleteFacture(int id) {
        if (!factureRepository.existsById(id)) {
            throw new RuntimeException("Facture introuvable avec l'ID: " + id);
        }
        factureRepository.deleteById(id);
    }

    /**
     * ✅ Calculer les totaux avec remise
     */
    private void calculerTotaux(FactureEntity facture) {
        double totalHT = 0.0;
        double totalTVA = 0.0;
        double totalRemise = 0.0;

        for (LigneFactureEntity ligne : facture.getLignesFacture()) {
            totalHT += ligne.getTotalHT() != null ? ligne.getTotalHT() : 0.0;
            totalTVA += ligne.getTotalTVA() != null ? ligne.getTotalTVA() : 0.0;
            totalRemise += ligne.getMontantRemise() != null ? ligne.getMontantRemise() : 0.0;
        }

        facture.setTotalHT(arrondir(totalHT));
        facture.setTotalTVA(arrondir(totalTVA));
        facture.setTotalTTC(arrondir(totalHT + totalTVA));
        facture.setTotalRemise(arrondir(totalRemise));
    }

    private String genererNumeroFacture() {
        String prefix = "FAC-" + LocalDate.now().toString().replace("-", "");
        long count = factureRepository.countByNumeroFactureStartingWith(prefix);
        return String.format("%s-%03d", prefix, count + 1);
    }

    private double arrondir(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }
}