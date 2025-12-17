package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.*;
import com.example.AppPfa.Repository.BonDeLivraisonFournisseurRepository;
import com.example.AppPfa.Repository.LigneBonDeLivraisonFournisseurRepository;
import com.example.AppPfa.Repository.LigneBon_de_receptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LigneBonDeLivraisonFournisseurService implements LigneBonDeLivraisonFournisseurManager {

    @Autowired
    private LigneBonDeLivraisonFournisseurRepository ligneBonDeLivraisonRepository;

    @Autowired
    private BonDeLivraisonFournisseurRepository bonDeLivraisonRepository;

    @Autowired
    private LigneBon_de_receptionRepository ligneBonDeReceptionRepository;

    /**
     * ✅ Récupérer une ligne par ID
     */
    @Override
    @Transactional(readOnly = true)
    public LigneBonDeLivraisonFournisseurEntity getLigneBonLivraisonById(Integer id) {
        return ligneBonDeLivraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne de bon de livraison non trouvée avec l'ID: " + id));
    }

    /**
     * ✅ Récupérer toutes les lignes
     */
    @Override
    @Transactional(readOnly = true)
    public List<LigneBonDeLivraisonFournisseurEntity> getAllLignesBonLivraison() {
        return ligneBonDeLivraisonRepository.findAll();
    }

    /**
     * ✅ Récupérer les lignes d'un bon de livraison spécifique
     */
    @Override
    @Transactional(readOnly = true)
    public List<LigneBonDeLivraisonFournisseurEntity> getLignesByBonLivraison(Integer bonLivraisonId) {
        return ligneBonDeLivraisonRepository.findByBonDeLivraisonFournisseurEntityId(bonLivraisonId);
    }

    /**
     * ✅ Ajouter une ligne de bon de livraison
     * Total = Prix Commande × Quantité Réception
     */
    @Override
    @Transactional
    public LigneBonDeLivraisonFournisseurEntity addLigneBonLivraison(Integer bonLivraisonId, Integer ligneBonReceptionId) {

        // 1️⃣ Récupérer le bon de livraison
        BonDeLivraisonFournisseurEntity bonLivraison = bonDeLivraisonRepository.findById(bonLivraisonId)
                .orElseThrow(() -> new RuntimeException("Bon de livraison non trouvé avec l'ID: " + bonLivraisonId));

        // 2️⃣ Récupérer la ligne de bon de réception
        LigneBonDeReceptionEntities ligneBonReception = ligneBonDeReceptionRepository.findById(ligneBonReceptionId)
                .orElseThrow(() -> new RuntimeException("Ligne de bon de réception non trouvée avec l'ID: " + ligneBonReceptionId));

        // 3️⃣ Récupérer la ligne de commande
        LigneCommandeAchatsEntity ligneCommande = ligneBonReception.getLigneCommandeAchatsEntity();
        if (ligneCommande == null) {
            throw new IllegalStateException("La ligne de bon de réception " + ligneBonReceptionId + " n'est pas liée à une ligne de commande.");
        }

        // 4️⃣ Extraire les données pour le calcul
        // PRIX depuis COMMANDE
        Double prixUnitaire = ligneCommande.getPrixUnitaire();

        // QUANTITÉ depuis RÉCEPTION
        Double quantiteReception = parseQuantite(ligneBonReception.getQtereception());

        // ✅ TVA depuis le Produit (au lieu de 20.0 en dur)
        Double tauxTVA = ligneCommande.getProduit().getTva().doubleValue();

        // 5️⃣ CALCULS : Total = Prix Commande × Quantité Réception
        Double totalHT = arrondir(prixUnitaire * quantiteReception);
        Double totalTVA = arrondir(totalHT * (tauxTVA / 100.0));
        Double totalTTC = arrondir(totalHT + totalTVA);

        // 6️⃣ Créer la nouvelle ligne
        LigneBonDeLivraisonFournisseurEntity nouvelleLigne = LigneBonDeLivraisonFournisseurEntity.builder()
                .bonDeLivraisonFournisseurEntity(bonLivraison)
                .ligneBonDeReceptionEntity(ligneBonReception)
                .ligneCommandeAchatsEntity(ligneCommande)
                .totalHT(totalHT)
                .totalTVA(totalTVA)
                .totalTTC(totalTTC)
                .build();

        // 7️⃣ Enregistrer la nouvelle ligne
        LigneBonDeLivraisonFournisseurEntity ligneEnregistree = ligneBonDeLivraisonRepository.save(nouvelleLigne);

        // 8️⃣ Recalculer les totaux du bon de livraison parent
        recalculerTotauxBonLivraison(bonLivraisonId);

        return ligneEnregistree;
    }

    /**
     * ✅ Supprimer une ligne de bon de livraison
     */
    @Override
    @Transactional
    public void deleteLigneBonLivraison(Integer id) {
        // Récupérer la ligne
        LigneBonDeLivraisonFournisseurEntity ligne = ligneBonDeLivraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne de bon de livraison non trouvée avec l'ID: " + id));

        Integer bonLivraisonId = ligne.getBonDeLivraisonFournisseurEntity().getId();

        // Supprimer la ligne
        ligneBonDeLivraisonRepository.deleteById(id);

        // Recalculer les totaux du bon de livraison parent
        recalculerTotauxBonLivraison(bonLivraisonId);
    }

    /**
     * ✅ Recalculer les totaux d'un bon de livraison
     */
    public void recalculerTotauxBonLivraison(Integer bonLivraisonId) {
        // Récupérer le bon de livraison
        BonDeLivraisonFournisseurEntity bonLivraison = bonDeLivraisonRepository.findById(bonLivraisonId)
                .orElseThrow(() -> new RuntimeException("Bon de livraison non trouvé avec l'ID: " + bonLivraisonId));

        // Récupérer toutes les lignes
        List<LigneBonDeLivraisonFournisseurEntity> lignes = ligneBonDeLivraisonRepository
                .findByBonDeLivraisonFournisseurEntityId(bonLivraisonId);

        // Calculer les totaux
        double totalHT = 0.0;
        double totalTVA = 0.0;

        for (LigneBonDeLivraisonFournisseurEntity ligne : lignes) {
            // ✅ Recalculer chaque ligne avec la TVA du produit
            LigneCommandeAchatsEntity ligneCommande = ligne.getLigneCommandeAchatsEntity();
            LigneBonDeReceptionEntities ligneBonReception = ligne.getLigneBonDeReceptionEntity();

            Double prixUnitaire = ligneCommande.getPrixUnitaire();
            Double quantiteReception = parseQuantite(ligneBonReception.getQtereception());

            // ✅ TVA depuis le Produit
            Double tauxTVA = ligneCommande.getProduit().getTva().doubleValue();

            Double ligneTotalHT = arrondir(prixUnitaire * quantiteReception);
            Double ligneTotalTVA = arrondir(ligneTotalHT * (tauxTVA / 100.0));
            Double ligneTotalTTC = arrondir(ligneTotalHT + ligneTotalTVA);

            // Mettre à jour la ligne
            ligne.setTotalHT(ligneTotalHT);
            ligne.setTotalTVA(ligneTotalTVA);
            ligne.setTotalTTC(ligneTotalTTC);

            totalHT += ligneTotalHT;
            totalTVA += ligneTotalTVA;
        }

        // Mettre à jour le bon de livraison
        bonLivraison.setTotalHT(arrondir(totalHT));
        bonLivraison.setTotalTVA(arrondir(totalTVA));
        bonLivraison.setTotalTTC(arrondir(totalHT + totalTVA));

        // Mettre à jour le statut
        bonLivraison.setStatut(calculerStatut(lignes));

        bonDeLivraisonRepository.save(bonLivraison);
    }

    /**
     * ✅ Calculer le statut
     */
    private BonDeLivraisonFournisseurEntity.StatutLivraison calculerStatut(
            List<LigneBonDeLivraisonFournisseurEntity> lignes) {

        if (lignes.isEmpty()) {
            return BonDeLivraisonFournisseurEntity.StatutLivraison.EN_ATTENTE;
        }

        boolean toutLivre = true;

        for (LigneBonDeLivraisonFournisseurEntity ligne : lignes) {
            Double qteCommandee = ligne.getLigneCommandeAchatsEntity().getQuantite();
            Double qteLivree = parseQuantite(ligne.getLigneBonDeReceptionEntity().getQtereception());

            if (!qteLivree.equals(qteCommandee)) {
                toutLivre = false;
                break;
            }
        }

        return toutLivre
                ? BonDeLivraisonFournisseurEntity.StatutLivraison.COMPLETEMENT_LIVRE
                : BonDeLivraisonFournisseurEntity.StatutLivraison.PARTIELLEMENT_LIVRE;
    }

    /**
     * ✅ Parse quantité String → Double
     */
    public Double parseQuantite(String qte) {
        if (qte == null || qte.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(qte.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * ✅ Arrondir à 2 décimales
     */
    public Double arrondir(Double valeur) {
        if (valeur == null) return 0.0;
        return Math.round(valeur * 100.0) / 100.0;
    }
}