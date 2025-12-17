package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.*;
import com.example.AppPfa.Repository.BonDeLivraisonFournisseurRepository;
import com.example.AppPfa.Repository.FactureFournisseurRepository;
import com.example.AppPfa.Repository.LigneFactureFournisseurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FactureFournisseurService implements FactureFournisseurManager {

    @Autowired
    private FactureFournisseurRepository factureFournisseurRepository;

    @Autowired
    private BonDeLivraisonFournisseurRepository bonDeLivraisonFournisseurRepository;

    @Autowired
    private LigneFactureFournisseurRepository ligneFactureFournisseurRepository;

    @Override
    public FactureFournisseurEntity addFactureFournisseur(FactureFournisseurEntity factureFournisseurEntity) {

        BonDeLivraisonFournisseurEntity bonLivraison = bonDeLivraisonFournisseurRepository.findById(
                factureFournisseurEntity.getBonDeLivraisonFournisseurEntity().getId()
        ).orElseThrow(() -> new RuntimeException(
                "Bon de livraison fournisseur introuvable avec l'ID: " + factureFournisseurEntity.getBonDeLivraisonFournisseurEntity().getId()
        ));

        if (factureFournisseurRepository.existsByBonDeLivraisonFournisseurEntity_Id(bonLivraison.getId())) {
            throw new RuntimeException(
                    "Une facture fournisseur existe déjà pour le bon de livraison: " + bonLivraison.getNumeroLivraison()
            );
        }

        if (factureFournisseurEntity.getNumeroFacture() == null || factureFournisseurEntity.getNumeroFacture().isEmpty()) {
            factureFournisseurEntity.setNumeroFacture(genererNumeroFactureFournisseur());
        }

        if (factureFournisseurEntity.getDateFacture() == null) {
            factureFournisseurEntity.setDateFacture(LocalDate.now());
        }

        factureFournisseurEntity.setBonDeLivraisonFournisseurEntity(bonLivraison);

        if (factureFournisseurEntity.getLigneFactureFournisseurEntities() == null) {
            factureFournisseurEntity.setLigneFactureFournisseurEntities(new ArrayList<>());
        }

        if (factureFournisseurEntity.getLigneFactureFournisseurEntities().isEmpty()) {
            for (LigneBonDeLivraisonFournisseurEntity ligneBL : bonLivraison.getLigneBonDeLivraisonEntities()) {
                LigneCommandeAchatsEntity ligneCommande = ligneBL.getLigneCommandeAchatsEntity();
                LigneBonDeReceptionEntities ligneReception = ligneBL.getLigneBonDeReceptionEntity();

                Double quantite = parseQuantite(ligneReception.getQtereception());
                Double prixUnitaire = ligneCommande.getPrixUnitaire();
                Double tauxTVA = getTvaFromProduit(ligneCommande);

                Double totalHT = quantite * prixUnitaire;
                Double totalTVA = totalHT * (tauxTVA / 100.0);
                Double totalTTC = totalHT + totalTVA;

                LigneFactureFournisseurEntity ligneFacture = new LigneFactureFournisseurEntity();
                ligneFacture.setFactureFournisseurEntity(factureFournisseurEntity);
                ligneFacture.setLigneBonDeLivraisonFournisseurEntity(ligneBL);
                ligneFacture.setLigneCommandeAchatsEntity(ligneCommande);
                ligneFacture.setTotalHT(totalHT);
                ligneFacture.setTotalTVA(totalTVA);
                ligneFacture.setTotalTTC(totalTTC);

                factureFournisseurEntity.getLigneFactureFournisseurEntities().add(ligneFacture);
            }
        } else {
            factureFournisseurEntity.getLigneFactureFournisseurEntities()
                    .forEach(ligne -> ligne.setFactureFournisseurEntity(factureFournisseurEntity));
        }

        calculerTotaux(factureFournisseurEntity);

        return factureFournisseurRepository.save(factureFournisseurEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public FactureFournisseurEntity getFactureFournisseurById(Integer id) {
        return factureFournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture fournisseur introuvable avec ID: " + id));
    }

    @Override
    @Transactional
    public FactureFournisseurEntity recalculerFactureFournisseurParBonLivraison(Integer bonLivraisonId) {

        Optional<FactureFournisseurEntity> factureOpt = factureFournisseurRepository
                .findByBonDeLivraisonFournisseurEntity_Id(bonLivraisonId);

        if (factureOpt.isEmpty()) {
            return null;
        }

        FactureFournisseurEntity facture = factureOpt.get();

        BonDeLivraisonFournisseurEntity bonLivraison = bonDeLivraisonFournisseurRepository.findById(bonLivraisonId)
                .orElseThrow(() -> new RuntimeException("Bon de livraison fournisseur introuvable"));

        ligneFactureFournisseurRepository.deleteByFactureFournisseurEntityId(facture.getId());
        facture.getLigneFactureFournisseurEntities().clear();

        double totalHT = 0.0;
        double totalTVA = 0.0;

        for (LigneBonDeLivraisonFournisseurEntity ligneBL : bonLivraison.getLigneBonDeLivraisonEntities()) {
            LigneCommandeAchatsEntity ligneCommande = ligneBL.getLigneCommandeAchatsEntity();
            LigneBonDeReceptionEntities ligneReception = ligneBL.getLigneBonDeReceptionEntity();

            Double quantite = parseQuantite(ligneReception.getQtereception());
            Double prixUnitaire = ligneCommande.getPrixUnitaire();
            Double tauxTVA = getTvaFromProduit(ligneCommande);

            Double ligneHT = quantite * prixUnitaire;
            Double ligneTVA = ligneHT * (tauxTVA / 100.0);
            Double ligneTTC = ligneHT + ligneTVA;

            LigneFactureFournisseurEntity ligneFacture = new LigneFactureFournisseurEntity();
            ligneFacture.setFactureFournisseurEntity(facture);
            ligneFacture.setLigneBonDeLivraisonFournisseurEntity(ligneBL);
            ligneFacture.setLigneCommandeAchatsEntity(ligneCommande);
            ligneFacture.setTotalHT(ligneHT);
            ligneFacture.setTotalTVA(ligneTVA);
            ligneFacture.setTotalTTC(ligneTTC);

            facture.getLigneFactureFournisseurEntities().add(ligneFacture);

            totalHT += ligneHT;
            totalTVA += ligneTVA;
        }

        facture.setTotalHT(totalHT);
        facture.setTotalTVA(totalTVA);
        facture.setTotalTTC(totalHT + totalTVA);

        return factureFournisseurRepository.save(facture);
    }

    @Override
    @Transactional
    public FactureFournisseurEntity recalculerFactureFournisseur(Integer factureId) {
        FactureFournisseurEntity facture = factureFournisseurRepository.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture fournisseur introuvable avec ID: " + factureId));

        return recalculerFactureFournisseurParBonLivraison(facture.getBonDeLivraisonFournisseurEntity().getId());
    }

    @Override
    public FactureFournisseurEntity updateFactureFournisseur(int id, FactureFournisseurEntity factureFournisseurEntity) {

        FactureFournisseurEntity factureExistante = factureFournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture fournisseur introuvable avec l'ID: " + id));

        if (factureFournisseurEntity.getNumeroFacture() != null) {
            factureExistante.setNumeroFacture(factureFournisseurEntity.getNumeroFacture());
        }

        if (factureFournisseurEntity.getDateFacture() != null) {
            factureExistante.setDateFacture(factureFournisseurEntity.getDateFacture());
        }

        if (factureFournisseurEntity.getLigneFactureFournisseurEntities() != null
                && !factureFournisseurEntity.getLigneFactureFournisseurEntities().isEmpty()) {

            factureExistante.getLigneFactureFournisseurEntities().clear();

            factureFournisseurEntity.getLigneFactureFournisseurEntities().forEach(ligne -> {
                ligne.setFactureFournisseurEntity(factureExistante);
                factureExistante.getLigneFactureFournisseurEntities().add(ligne);
            });
        }

        calculerTotaux(factureExistante);

        return factureFournisseurRepository.save(factureExistante);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FactureFournisseurEntity> getAllFactureFournisseur() {
        return factureFournisseurRepository.findAll();
    }

    @Override
    public void deleteFactureFournisseur(int id) {
        if (!factureFournisseurRepository.existsById(id)) {
            throw new RuntimeException("Facture fournisseur introuvable avec l'ID: " + id);
        }
        factureFournisseurRepository.deleteById(id);
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
     * ✅ Parser la quantité String → Double
     */
    private Double parseQuantite(String qte) {
        if (qte == null || qte.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(qte.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * ✅ Calculer les totaux de la facture
     */
    private void calculerTotaux(FactureFournisseurEntity facture) {
        double totalHT = 0.0;
        double totalTVA = 0.0;
        double totalTTC = 0.0;

        for (LigneFactureFournisseurEntity ligne : facture.getLigneFactureFournisseurEntities()) {
            totalHT += ligne.getTotalHT() != null ? ligne.getTotalHT() : 0.0;
            totalTVA += ligne.getTotalTVA() != null ? ligne.getTotalTVA() : 0.0;
            totalTTC += ligne.getTotalTTC() != null ? ligne.getTotalTTC() : 0.0;
        }

        facture.setTotalHT(totalHT);
        facture.setTotalTVA(totalTVA);
        facture.setTotalTTC(totalTTC);
    }

    /**
     * ✅ Générer un numéro de facture unique
     */
    private String genererNumeroFactureFournisseur() {
        String prefix = "FAC-FRN-" + LocalDate.now().toString().replace("-", "");
        long count = factureFournisseurRepository.countByNumeroFactureStartingWith(prefix);
        return String.format("%s-%03d", prefix, count + 1);

    }
    // ✅ AJOUTER CES MÉTHODES DANS FactureFournisseurService.java

    public FactureFournisseurEntity uploadPdf(Integer id, MultipartFile file) throws IOException {
        FactureFournisseurEntity facture = factureFournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture fournisseur introuvable"));

        facture.setPdfFile(file.getBytes());
        facture.setPdfFileName(file.getOriginalFilename());
        facture.setPdfUploadDate(LocalDateTime.now());

        return factureFournisseurRepository.save(facture);
    }

    public void deletePdf(Integer id) {
        FactureFournisseurEntity facture = factureFournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture fournisseur introuvable"));

        facture.setPdfFile(null);
        facture.setPdfFileName(null);
        facture.setPdfUploadDate(null);

        factureFournisseurRepository.save(facture);
    }


}