package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.*;
import com.example.AppPfa.Repository.FactureFournisseurRepository;
import com.example.AppPfa.Repository.LigneBonDeLivraisonFournisseurRepository;
import com.example.AppPfa.Repository.LigneFactureFournisseurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LigneFactureFournisseurService implements LigneFactureFournisseurManager {

    @Autowired
    private LigneFactureFournisseurRepository ligneFactureFournisseurRepository;

    @Autowired
    private FactureFournisseurRepository factureFournisseurRepository;

    @Autowired
    private LigneBonDeLivraisonFournisseurRepository ligneBonDeLivraisonFournisseurRepository;

    @Override
    @Transactional(readOnly = true)
    public LigneFactureFournisseurEntity getLigneFactureFournisseurById(Integer id) {
        return ligneFactureFournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne de facture fournisseur introuvable avec ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LigneFactureFournisseurEntity> getAllLignesFactureFournisseur() {
        return ligneFactureFournisseurRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LigneFactureFournisseurEntity> getLignesByFactureFournisseurId(Integer factureId) {
        return ligneFactureFournisseurRepository.findByFactureFournisseurEntityId(factureId);
    }

    @Override
    @Transactional
    public LigneFactureFournisseurEntity addLigneFactureFournisseur(Integer factureId, Integer ligneBonLivraisonFournisseurId) {

        FactureFournisseurEntity facture = factureFournisseurRepository.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture fournisseur introuvable avec ID : " + factureId));

        LigneBonDeLivraisonFournisseurEntity ligneBonLivraison = ligneBonDeLivraisonFournisseurRepository.findById(ligneBonLivraisonFournisseurId)
                .orElseThrow(() -> new RuntimeException("Ligne de bon de livraison fournisseur introuvable avec ID : " + ligneBonLivraisonFournisseurId));

        LigneCommandeAchatsEntity ligneCommandeAchats = ligneBonLivraison.getLigneCommandeAchatsEntity();
        if (ligneCommandeAchats == null) {
            throw new IllegalStateException("La ligne de bon de livraison fournisseur n'est pas liée à une ligne de commande achats.");
        }

        // ✅ Calcul des totaux avec TVA OBLIGATOIRE depuis le Produit
        Double totalHT = calculerTotalHT(ligneBonLivraison);
        Double tauxTVA = getTvaFromProduit(ligneCommandeAchats);
        Double totalTVA = totalHT * (tauxTVA / 100.0);
        Double totalTTC = totalHT + totalTVA;

        LigneFactureFournisseurEntity nouvelleLigne = new LigneFactureFournisseurEntity();
        nouvelleLigne.setFactureFournisseurEntity(facture);
        nouvelleLigne.setLigneBonDeLivraisonFournisseurEntity(ligneBonLivraison);
        nouvelleLigne.setLigneCommandeAchatsEntity(ligneCommandeAchats);
        nouvelleLigne.setTotalHT(totalHT);
        nouvelleLigne.setTotalTVA(totalTVA);
        nouvelleLigne.setTotalTTC(totalTTC);

        LigneFactureFournisseurEntity saved = ligneFactureFournisseurRepository.save(nouvelleLigne);

        recalculerTotauxFactureFournisseur(factureId);

        return saved;
    }

    @Override
    @Transactional
    public void deleteLigneFactureFournisseur(Integer id) {

        LigneFactureFournisseurEntity ligne = ligneFactureFournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne facture fournisseur introuvable"));

        Integer factureId = ligne.getFactureFournisseurEntity().getId();

        ligneFactureFournisseurRepository.delete(ligne);

        recalculerTotauxFactureFournisseur(factureId);
    }

    // =============================================
    // ✅ MÉTHODES UTILITAIRES PRIVÉES
    // =============================================

    /**
     * ✅ Récupérer la TVA depuis le Produit (OBLIGATOIRE - PAS DE VALEUR PAR DÉFAUT)
     */
    private Double getTvaFromProduit(LigneCommandeAchatsEntity ligneCommande) {
        if (ligneCommande == null) {
            throw new RuntimeException("Ligne de commande introuvable");
        }

        if (ligneCommande.getProduit() == null) {
            throw new RuntimeException("Produit introuvable pour la ligne de commande");
        }

        if (ligneCommande.getProduit().getTva() == null) {
            throw new RuntimeException(
                    "TVA introuvable pour le produit: " + ligneCommande.getProduit().getReferences()
            );
        }

        return ligneCommande.getProduit().getTva().doubleValue();
    }

    /**
     * ✅ Calculer le Total HT depuis une ligne BL
     */
    private Double calculerTotalHT(LigneBonDeLivraisonFournisseurEntity ligneBL) {

        if (ligneBL.getTotalHT() != null && ligneBL.getTotalHT() > 0) {
            return ligneBL.getTotalHT();
        }

        if (ligneBL.getLigneCommandeAchatsEntity() != null) {
            LigneCommandeAchatsEntity lc = ligneBL.getLigneCommandeAchatsEntity();

            if (lc.getTotalHT() != null && lc.getTotalHT() > 0) {
                return lc.getTotalHT();
            }

            Double qte = lc.getQuantite() != null ? lc.getQuantite() : 0.0;
            Double pu = lc.getPrixUnitaire() != null ? lc.getPrixUnitaire() : 0.0;
            return qte * pu;
        }

        if (ligneBL.getLigneBonDeReceptionEntity() != null) {
            LigneBonDeReceptionEntities lr = ligneBL.getLigneBonDeReceptionEntity();

            if (lr.getLigneCommandeAchatsEntity() != null) {
                LigneCommandeAchatsEntity lc = lr.getLigneCommandeAchatsEntity();

                Double qte = 0.0;
                if (lr.getQtereception() != null) {
                    try {
                        qte = Double.parseDouble(lr.getQtereception());
                    } catch (NumberFormatException e) {
                        qte = lc.getQuantite() != null ? lc.getQuantite() : 0.0;
                    }
                }

                Double pu = lc.getPrixUnitaire() != null ? lc.getPrixUnitaire() : 0.0;
                return qte * pu;
            }
        }

        return 0.0;
    }

    /**
     * ✅ Recalcul total facture fournisseur avec TVA OBLIGATOIRE depuis Produit
     */
    private void recalculerTotauxFactureFournisseur(Integer factureId) {

        FactureFournisseurEntity facture = factureFournisseurRepository.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture fournisseur introuvable avec ID : " + factureId));

        List<LigneFactureFournisseurEntity> lignes = ligneFactureFournisseurRepository.findByFactureFournisseurEntityId(factureId);

        double totalHT = 0.0;
        double totalTVA = 0.0;

        for (LigneFactureFournisseurEntity ligne : lignes) {
            LigneCommandeAchatsEntity ligneCommande = ligne.getLigneCommandeAchatsEntity();

            Double ligneHT = ligne.getTotalHT() != null ? ligne.getTotalHT() : 0.0;
            Double tauxTVA = getTvaFromProduit(ligneCommande);
            Double ligneTVA = ligneHT * (tauxTVA / 100.0);
            Double ligneTTC = ligneHT + ligneTVA;

            ligne.setTotalTVA(ligneTVA);
            ligne.setTotalTTC(ligneTTC);

            totalHT += ligneHT;
            totalTVA += ligneTVA;
        }

        facture.setTotalHT(totalHT);
        facture.setTotalTVA(totalTVA);
        facture.setTotalTTC(totalHT + totalTVA);

        factureFournisseurRepository.save(facture);
    }
}