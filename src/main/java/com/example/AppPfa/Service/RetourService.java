package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.*;
import com.example.AppPfa.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RetourService implements RetourManager {

    private final RetourProduitRepository retourProduitRepository;
    private final LigneRetourRepository ligneRetourRepository;
    private final ClientRepository clientRepository;
    private final FournisseurRepository fournisseurRepository;
    private final BonLivraisonRepository bonLivraisonRepository;
    private final LigneBonLivraisonRepository ligneBonLivraisonRepository;
    private final BonDeReceptionRepository bonDeReceptionRepository;
    private final LigneBon_de_receptionRepository ligneBonDeReceptionRepository;
    private final ProduitRepository produitRepository;
    private final StockManager stockManager;
    private final AvoirManager avoirManager;

    // ========================================================================
    // 1. CRÉER RETOUR CLIENT
    // ========================================================================
    @Override
    public RetourProduitEntity creerRetourClient(Integer clientId, Integer bonLivraisonId,
                                                 List<Map<String, Object>> lignesData, String motif) {
        log.info("Création retour client - Client ID: {}, BL ID: {}", clientId, bonLivraisonId);

        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable: " + clientId));

        BonLivraisonEntity bonLivraison = bonLivraisonRepository.findById(bonLivraisonId)
                .orElseThrow(() -> new RuntimeException("Bon Livraison introuvable: " + bonLivraisonId));

        RetourProduitEntity retour = RetourProduitEntity.builder()
                .numeroRetour(generateNumeroRetour())
                .dateRetour(LocalDate.now())
                .typeRetour(RetourProduitEntity.TypeRetour.RETOUR_CLIENT)
                .client(client)
                .bonLivraison(bonLivraison)
                .motifRetour(motif)
                .statut(RetourProduitEntity.StatutRetour.EN_ATTENTE)
                .totalHT(0.0)
                .totalTVA(0.0)
                .totalTTC(0.0)
                .lignesRetour(new ArrayList<>())
                .build();

        retour = retourProduitRepository.save(retour);
        processLignesClient(retour, lignesData);
        retour = retourProduitRepository.save(retour);

        log.info("✅ Retour client créé: {} - Total TTC: {}", retour.getNumeroRetour(), retour.getTotalTTC());
        return retour;
    }

    // ========================================================================
    // 2. CRÉER RETOUR FOURNISSEUR
    // ========================================================================
    @Override
    public RetourProduitEntity creerRetourFournisseur(Integer fournisseurId, Integer bonReceptionId,
                                                      List<Map<String, Object>> lignesData, String motif) {
        log.info("Création retour fournisseur - Fournisseur ID: {}, BR ID: {}", fournisseurId, bonReceptionId);

        FournisseurEntity fournisseur = fournisseurRepository.findById(fournisseurId)
                .orElseThrow(() -> new RuntimeException("Fournisseur introuvable: " + fournisseurId));

        BonDeReceptionEntity bonReception = bonDeReceptionRepository.findById(bonReceptionId)
                .orElseThrow(() -> new RuntimeException("Bon Réception introuvable: " + bonReceptionId));

        RetourProduitEntity retour = RetourProduitEntity.builder()
                .numeroRetour(generateNumeroRetour())
                .dateRetour(LocalDate.now())
                .typeRetour(RetourProduitEntity.TypeRetour.RETOUR_FOURNISSEUR)
                .fournisseur(fournisseur)
                .bonDeReception(bonReception)
                .motifRetour(motif)
                .statut(RetourProduitEntity.StatutRetour.EN_ATTENTE)
                .totalHT(0.0)
                .totalTVA(0.0)
                .totalTTC(0.0)
                .lignesRetour(new ArrayList<>())
                .build();

        retour = retourProduitRepository.save(retour);
        processLignesFournisseur(retour, lignesData);
        retour = retourProduitRepository.save(retour);

        log.info("✅ Retour fournisseur créé: {} - Total TTC: {}", retour.getNumeroRetour(), retour.getTotalTTC());
        return retour;
    }

    // ========================================================================
    // 3. TRAITEMENT DES LIGNES CLIENT  (INTÈGRE LA REMISE DU BL)
    // ========================================================================
    private void processLignesClient(RetourProduitEntity retour, List<Map<String, Object>> lignesData) {
        double totalHT = 0.0;
        double totalTVA = 0.0;
        double totalTTC = 0.0;

        log.info("Traitement de {} lignes pour retour client", lignesData.size());

        for (Map<String, Object> data : lignesData) {
            Integer ligneBlId = getInteger(data, "ligneBonLivraisonId");
            Double quantiteRetournee = getDouble(data, "quantiteRetournee");
            String observation = getString(data, "observation");
            String etatProduitStr = getString(data, "etatProduit");
            String actionRetourStr = getString(data, "actionRetour");

            if (ligneBlId == null) {
                throw new RuntimeException("ID de ligne BL manquant");
            }
            if (quantiteRetournee == null || quantiteRetournee <= 0) {
                throw new RuntimeException("Quantité invalide pour ligne BL ID: " + ligneBlId);
            }

            LigneBonLivraisonEntity ligneBL = ligneBonLivraisonRepository.findById(ligneBlId)
                    .orElseThrow(() -> new RuntimeException("Ligne BL introuvable: " + ligneBlId));

            // ═══════════════════════════════════════════════════════════════
            // ✅ Déterminer le produit réellement livré (échange ou non)
            // ═══════════════════════════════════════════════════════════════
            LigneBonSortieEntity ligneBonSortie = ligneBL.getLigneBonSortie();
            ProduitEntity produitARetourner;
            ProduitEntity produitOriginalCommande;
            boolean wasEchange = false;

            if (ligneBonSortie != null && ligneBonSortie.getProduitEchange() != null) {
                produitARetourner = ligneBonSortie.getProduitEchange();
                produitOriginalCommande = ligneBL.getLigneCommande().getProduit();
                wasEchange = true;

                log.info("🔄 ECHANGE (Retour) BL {} - Cmd: {} -> Livré: {}",
                        ligneBlId,
                        produitOriginalCommande.getDesignation(),
                        produitARetourner.getDesignation());
            } else {
                produitARetourner = ligneBL.getLigneCommande().getProduit();
                produitOriginalCommande = produitARetourner;

                log.info("📦 Sortie normale (Retour) BL {} - Produit: {}",
                        ligneBlId,
                        produitARetourner.getDesignation());
            }

            if (produitARetourner == null) {
                throw new RuntimeException("Produit introuvable dans ligne BL: " + ligneBlId);
            }

            // ═══════════════════════════════════════════════════════════════
            // ✅ CALCUL PRIX UNIT NET HT (APRÈS REMISE) À PARTIR DU BL
            //    → pour que le retour rembourse le montant remisé
            // ═══════════════════════════════════════════════════════════════
            Double totalHTLigneBL = ligneBL.getTotalHT() != null ? ligneBL.getTotalHT() : 0.0;
            Double quantiteLivreeBL = (ligneBL.getLigneBonSortie() != null
                    && ligneBL.getLigneBonSortie().getQuantiteSortie() != null)
                    ? ligneBL.getLigneBonSortie().getQuantiteSortie()
                    : null;

            Double prixUnitaireNetHT;
            if (quantiteLivreeBL != null && quantiteLivreeBL > 0) {
                // ✅ Prix unitaire NET (après remise)
                prixUnitaireNetHT = totalHTLigneBL / quantiteLivreeBL;
            } else {
                // Fallback: prix unitaire de la commande (sans remise)
                prixUnitaireNetHT = ligneBL.getLigneCommande().getPrixUnitaire() != null
                        ? ligneBL.getLigneCommande().getPrixUnitaire()
                        : 0.0;
            }

            double tvaPourcent = (produitARetourner.getTva() != null) ? produitARetourner.getTva() : 20.0;

            // ═══════════════════════════════════════════════════════════════
            // ✅ CRÉER LIGNE RETOUR (AVEC PRIX NET)
            // ═══════════════════════════════════════════════════════════════
            LigneRetourEntity ligne = LigneRetourEntity.builder()
                    .retourProduit(retour)
                    .produit(produitARetourner)
                    .produitEchange(wasEchange ? produitOriginalCommande : null)
                    .quantiteRetournee(quantiteRetournee)
                    .prixUnitaire(prixUnitaireNetHT)   // pour info
                    .prixUnitaireHT(prixUnitaireNetHT) // utilisé dans le calcul
                    .tva(tvaPourcent)
                    .observation(observation != null ? observation : "")
                    .isEchange(wasEchange)
                    .etatProduit(parseEnum(
                            LigneRetourEntity.EtatProduit.class,
                            etatProduitStr,
                            LigneRetourEntity.EtatProduit.BON_ETAT
                    ))
                    .actionRetour(parseEnum(
                            LigneRetourEntity.ActionRetour.class,
                            actionRetourStr,
                            LigneRetourEntity.ActionRetour.REMBOURSEMENT
                    ))
                    .totalHT(0.0)
                    .totalTVA(0.0)
                    .totalTTC(0.0)
                    .build();

            // ➜ Calculer totaux à partir du prix NET HT
            calculerTotauxLigne(ligne);

            ligne = ligneRetourRepository.save(ligne);
            retour.getLignesRetour().add(ligne);

            totalHT += ligne.getTotalHT();
            totalTVA += ligne.getTotalTVA();
            totalTTC += ligne.getTotalTTC();

            log.debug("✅ Ligne retour créée - Produit: {}, Qté: {}, Total HT: {}, TTC: {}",
                    produitARetourner.getDesignation(), quantiteRetournee,
                    ligne.getTotalHT(), ligne.getTotalTTC());
        }

        retour.setTotalHT(arrondir(totalHT));
        retour.setTotalTVA(arrondir(totalTVA));
        retour.setTotalTTC(arrondir(totalTTC));
    }

    // ========================================================================
    // 4. TRAITEMENT DES LIGNES FOURNISSEUR (inchangé)
    // ========================================================================
    private void processLignesFournisseur(RetourProduitEntity retour, List<Map<String, Object>> lignesData) {
        double totalHT = 0.0;
        double totalTVA = 0.0;
        double totalTTC = 0.0;

        log.info("Traitement de {} lignes pour retour fournisseur", lignesData.size());

        for (Map<String, Object> data : lignesData) {
            Integer ligneBrId = getInteger(data, "ligneBonReceptionId");
            Double quantite = getDouble(data, "quantiteRetournee");
            String observation = getString(data, "observation");
            String etatProduitStr = getString(data, "etatProduit");
            String actionRetourStr = getString(data, "actionRetour");

            if (ligneBrId == null) {
                throw new RuntimeException("ID de ligne BR manquant");
            }
            if (quantite == null || quantite <= 0) {
                throw new RuntimeException("Quantité invalide pour ligne BR ID: " + ligneBrId);
            }

            LigneBonDeReceptionEntities source = ligneBonDeReceptionRepository.findById(ligneBrId)
                    .orElseThrow(() -> new RuntimeException("Ligne BR introuvable: " + ligneBrId));

            ProduitEntity produit = source.getLigneCommandeAchatsEntity().getProduit();
            Double prixUnitaire = source.getLigneCommandeAchatsEntity().getPrixUnitaire();

            if (produit == null) {
                throw new RuntimeException("Produit introuvable dans ligne BR: " + ligneBrId);
            }

            double tvaPourcent = (produit.getTva() != null) ? produit.getTva() : 20.0;
            Double prixUnitaireHT = (prixUnitaire != null) ? prixUnitaire : 0.0;

            // Vérifier stock avant création
            stockManager.verifierStockOuException(produit.getId(), quantite);

            LigneRetourEntity ligne = LigneRetourEntity.builder()
                    .retourProduit(retour)
                    .produit(produit)
                    .produitEchange(null)
                    .quantiteRetournee(quantite)
                    .prixUnitaire(prixUnitaireHT)
                    .prixUnitaireHT(prixUnitaireHT)
                    .tva(tvaPourcent)
                    .observation(observation != null ? observation : "")
                    .isEchange(false)
                    .etatProduit(parseEnum(
                            LigneRetourEntity.EtatProduit.class,
                            etatProduitStr,
                            LigneRetourEntity.EtatProduit.BON_ETAT
                    ))
                    .actionRetour(parseEnum(
                            LigneRetourEntity.ActionRetour.class,
                            actionRetourStr,
                            LigneRetourEntity.ActionRetour.REMBOURSEMENT
                    ))
                    .totalHT(0.0)
                    .totalTVA(0.0)
                    .totalTTC(0.0)
                    .build();

            calculerTotauxLigne(ligne);

            ligne = ligneRetourRepository.save(ligne);
            retour.getLignesRetour().add(ligne);

            totalHT += ligne.getTotalHT();
            totalTVA += ligne.getTotalTVA();
            totalTTC += ligne.getTotalTTC();

            log.debug("✅ Ligne retour fournisseur créée - Produit: {}, Qté: {}, Total TTC: {}",
                    produit.getDesignation(), quantite, ligne.getTotalTTC());
        }

        retour.setTotalHT(arrondir(totalHT));
        retour.setTotalTVA(arrondir(totalTVA));
        retour.setTotalTTC(arrondir(totalTTC));
    }

    // ========================================================================
    // 5. VALIDATION / ANNULATION  (inchangé)
    // ========================================================================
    @Override
    public RetourProduitEntity validerRetour(Integer retourId, boolean genererAvoir) {
        RetourProduitEntity retour = getRetourById(retourId);

        if (retour.getStatut() != RetourProduitEntity.StatutRetour.EN_ATTENTE) {
            throw new RuntimeException("Ce retour est déjà traité (Statut: " + retour.getStatut() + ")");
        }

        log.info("═══════════════════════════════════════════════════════════");
        log.info("🔄 Validation du retour {} (Type: {})", retour.getNumeroRetour(), retour.getTypeRetour());

        for (LigneRetourEntity ligne : retour.getLignesRetour()) {

            if (retour.getTypeRetour() == RetourProduitEntity.TypeRetour.RETOUR_CLIENT) {
                if (ligne.getEtatProduit() == LigneRetourEntity.EtatProduit.BON_ETAT ||
                        ligne.getActionRetour() == LigneRetourEntity.ActionRetour.REINTEGRATION_STOCK) {

                    log.info("📦 Réintégration stock: {} (+{})",
                            ligne.getProduit().getReferences(),
                            ligne.getQuantiteRetournee());

                    stockManager.ajouterStock(
                            ligne.getProduit().getId(),
                            ligne.getQuantiteRetournee(),
                            MouvementStockEntity.TypeMouvement.RETOUR_CLIENT,
                            "Retour Client " + retour.getNumeroRetour() +
                                    (ligne.getIsEchange() ? " (était un échange)" : "")
                    );
                }

                if (ligne.getActionRetour() == LigneRetourEntity.ActionRetour.ECHANGE
                        && ligne.getProduitEchange() != null) {

                    log.info("🔄 Nouvel échange demandé: sortie de {} ({})",
                            ligne.getProduitEchange().getReferences(),
                            ligne.getQuantiteRetournee());

                    stockManager.verifierStockOuException(
                            ligne.getProduitEchange().getId(),
                            ligne.getQuantiteRetournee()
                    );

                    stockManager.retirerStock(
                            ligne.getProduitEchange().getId(),
                            ligne.getQuantiteRetournee(),
                            MouvementStockEntity.TypeMouvement.SORTIE_VENTE,
                            "Échange suite au retour " + retour.getNumeroRetour()
                    );
                }

            } else if (retour.getTypeRetour() == RetourProduitEntity.TypeRetour.RETOUR_FOURNISSEUR) {

                log.info("📤 Retour fournisseur: {} (-{})",
                        ligne.getProduit().getReferences(),
                        ligne.getQuantiteRetournee());

                stockManager.retirerStock(
                        ligne.getProduit().getId(),
                        ligne.getQuantiteRetournee(),
                        MouvementStockEntity.TypeMouvement.RETOUR_FOURNISSEUR,
                        "Retour Fournisseur " + retour.getNumeroRetour()
                );
            }
        }

        if (genererAvoir) {
            try {
                AvoirEntity avoir = avoirManager.creerAvoirDepuisRetour(retour);
                retour.setAvoir(avoir);
                retour.setStatut(RetourProduitEntity.StatutRetour.AVOIR_GENERE);
                log.info("✅ Avoir généré: {}", avoir.getNumeroAvoir());
            } catch (Exception e) {
                log.warn("⚠️ Erreur lors de la génération de l'avoir: {}", e.getMessage());
                retour.setStatut(RetourProduitEntity.StatutRetour.VALIDE);
            }
        } else {
            retour.setStatut(RetourProduitEntity.StatutRetour.VALIDE);
        }

        log.info("✅ Retour {} validé avec succès", retour.getNumeroRetour());
        log.info("═══════════════════════════════════════════════════════════");

        return retourProduitRepository.save(retour);
    }

    @Override
    public RetourProduitEntity annulerRetour(Integer retourId) {
        RetourProduitEntity retour = getRetourById(retourId);

        if (retour.getStatut() == RetourProduitEntity.StatutRetour.ANNULE) {
            throw new RuntimeException("Retour déjà annulé");
        }

        log.info("═══════════════════════════════════════════════════════════");
        log.info("❌ Annulation du retour: {}", retour.getNumeroRetour());

        if (retour.getStatut() == RetourProduitEntity.StatutRetour.VALIDE ||
                retour.getStatut() == RetourProduitEntity.StatutRetour.AVOIR_GENERE) {

            for (LigneRetourEntity ligne : retour.getLignesRetour()) {
                inverserMouvementStock(retour, ligne);
            }

            if (retour.getAvoir() != null) {
                avoirManager.annulerAvoir(retour.getAvoir().getId());
            }
        }

        retour.setStatut(RetourProduitEntity.StatutRetour.ANNULE);

        log.info("✅ Retour {} annulé avec succès", retour.getNumeroRetour());
        log.info("═══════════════════════════════════════════════════════════");

        return retourProduitRepository.save(retour);
    }

    private void inverserMouvementStock(RetourProduitEntity retour, LigneRetourEntity ligne) {
        if (retour.getTypeRetour() == RetourProduitEntity.TypeRetour.RETOUR_CLIENT) {

            if (ligne.getEtatProduit() == LigneRetourEntity.EtatProduit.BON_ETAT ||
                    ligne.getActionRetour() == LigneRetourEntity.ActionRetour.REINTEGRATION_STOCK) {

                log.info("🔄 Annulation réintégration: {} (-{})",
                        ligne.getProduit().getReferences(),
                        ligne.getQuantiteRetournee());

                stockManager.retirerStock(
                        ligne.getProduit().getId(),
                        ligne.getQuantiteRetournee(),
                        MouvementStockEntity.TypeMouvement.AJUSTEMENT_NEGATIF,
                        "Annulation Retour " + retour.getNumeroRetour()
                );
            }

            if (ligne.getActionRetour() == LigneRetourEntity.ActionRetour.ECHANGE
                    && ligne.getProduitEchange() != null) {

                log.info("🔄 Annulation échange: {} (+{})",
                        ligne.getProduitEchange().getReferences(),
                        ligne.getQuantiteRetournee());

                stockManager.ajouterStock(
                        ligne.getProduitEchange().getId(),
                        ligne.getQuantiteRetournee(),
                        MouvementStockEntity.TypeMouvement.AJUSTEMENT_POSITIF,
                        "Annulation Échange " + retour.getNumeroRetour()
                );
            }

        } else {
            log.info("🔄 Annulation retour fournisseur: {} (+{})",
                    ligne.getProduit().getReferences(),
                    ligne.getQuantiteRetournee());

            stockManager.ajouterStock(
                    ligne.getProduit().getId(),
                    ligne.getQuantiteRetournee(),
                    MouvementStockEntity.TypeMouvement.AJUSTEMENT_POSITIF,
                    "Annulation Retour Frs " + retour.getNumeroRetour()
            );
        }
    }

    // ========================================================================
    // 7. GETTERS
    // ========================================================================

    @Override
    public List<RetourProduitEntity> getAllRetours() {
        return retourProduitRepository.findAll();
    }

    @Override
    public RetourProduitEntity getRetourById(Integer id) {
        return retourProduitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Retour introuvable: " + id));
    }

    // ========================================================================
    // 8. UTILITAIRES
    // ========================================================================

    private void calculerTotauxLigne(LigneRetourEntity ligne) {
        double quantite = ligne.getQuantiteRetournee() != null ? ligne.getQuantiteRetournee() : 0.0;

        double prixHT = (ligne.getPrixUnitaireHT() != null && ligne.getPrixUnitaireHT() > 0)
                ? ligne.getPrixUnitaireHT()
                : (ligne.getPrixUnitaire() != null ? ligne.getPrixUnitaire() : 0.0);

        double ht = quantite * prixHT;

        double tvaPourcent = (ligne.getTva() != null) ? ligne.getTva() : 20.0;
        double montantTva = ht * (tvaPourcent / 100.0);

        ligne.setTotalHT(arrondir(ht));
        ligne.setTotalTVA(arrondir(montantTva));
        ligne.setTotalTTC(arrondir(ht + montantTva));
    }

    private double arrondir(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }

    private String generateNumeroRetour() {
        return "RET-" + java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now())
                + "-" + String.format("%04d", System.currentTimeMillis() % 10000);
    }

    // Helpers de parsing
    private Integer getInteger(Map<String, Object> map, String key) {
        if (map == null || key == null) return null;
        Object val = map.get(key);
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).intValue();
        if (val instanceof String) {
            try {
                return Integer.parseInt((String) val);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Double getDouble(Map<String, Object> map, String key) {
        if (map == null || key == null) return null;
        Object val = map.get(key);
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).doubleValue();
        if (val instanceof String) {
            try {
                return Double.parseDouble((String) val);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private String getString(Map<String, Object> map, String key) {
        if (map == null || key == null) return null;
        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }

    private <E extends Enum<E>> E parseEnum(Class<E> enumType, String value, E defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return Enum.valueOf(enumType, value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
}