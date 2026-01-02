package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.EtatduStock;
import com.example.AppPfa.DAO.Entity.MouvementStockEntity;
import com.example.AppPfa.DAO.Entity.ProduitEntity;
import com.example.AppPfa.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StockService implements StockManager {

    private final ProduitRepository produitRepository;
    private final EtatduStockRepository etatduStockRepository;
    private final LigneBon_de_receptionRepository receptionRepo;
    private final LigneInventaireRepository inventaireRepo;
    private final LigneBonSortieRepository sortieRepo;
    private final LigneRetourRepository ligneRetourRepo;
    private final MouvementStockRepository mouvementStockRepository;

    // ═══════════════════════════════════════════════════════════════
    // ✅ CALCUL STOCK RÉEL
    // ═══════════════════════════════════════════════════════════════

    @Override
    public Double getStockReel(Integer produitId) {
        // ✅ ENTRÉES
        Double entrees = receptionRepo.sumQteReceptionByProduit(produitId);
        Double inventaires = inventaireRepo.sumInventaireByProduit(produitId);
        Double retoursClients = ligneRetourRepo.sumRetoursClientsValides(produitId);

        // ✅ SORTIES
        Double sortiesNormales = sortieRepo.sumSortiesNormales(produitId);
        Double sortiesEchangees = sortieRepo.sumSortiesEchangees(produitId);
        Double retoursFournisseur = ligneRetourRepo.sumRetoursFournisseurValides(produitId);

        double total = safeDouble(entrees)
                + safeDouble(inventaires)
                + safeDouble(retoursClients)
                - safeDouble(sortiesNormales)
                - safeDouble(sortiesEchangees)
                - safeDouble(retoursFournisseur);

        log.debug("═══════════════════════════════════════════════════════════");
        log.debug("📦 Stock Produit {} :", produitId);
        log.debug("   + Réceptions      : {}", safeDouble(entrees));
        log.debug("   + Inventaires     : {}", safeDouble(inventaires));
        log.debug("   + Retours Clients : {}", safeDouble(retoursClients));
        log.debug("   - Sorties Normales: {}", safeDouble(sortiesNormales));
        log.debug("   - Sorties Échange : {}", safeDouble(sortiesEchangees));
        log.debug("   - Retours Fourn.  : {}", safeDouble(retoursFournisseur));
        log.debug("   = TOTAL           : {}", total);

        return total;
    }

    @Override
    public Double calculerStockFinal(Integer produitId) {
        return getStockReel(produitId);
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ AJOUTER AU STOCK
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public void ajouterStock(Integer produitId, Double quantite,
                             MouvementStockEntity.TypeMouvement type,
                             String motif) {

        validateQuantite(quantite);

        ProduitEntity produit = getProduitOrThrow(produitId);
        Double stockAvant = getStockReelFromEtat(produitId);
        Double stockApres = stockAvant + quantite;

        // ✅ Update EtatduStock
        int updated = etatduStockRepository.ajouterAuStock(produitId, quantite);
        if (updated == 0) {
            creerEtatStockSiAbsent(produit, quantite);
        }

        // ✅ Créer mouvement
        MouvementStockEntity mouvement = MouvementStockEntity.builder()
                .reference(generateReference())
                .produit(produit)
                .typeMouvement(type)
                .quantite(quantite)
                .quantiteAvant(stockAvant)
                .quantiteApres(stockApres)
                .motif(motif)
                .dateMouvement(LocalDateTime.now())
                .statut(MouvementStockEntity.StatutMouvement.VALIDE)
                .build();

        mouvementStockRepository.save(mouvement);

        log.info("✅ STOCK +{} → Produit {} ({}) | Type: {} | {} → {}",
                quantite, produit.getReferences(), produit.getDesignation(),
                type, stockAvant, stockApres);
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ RETIRER DU STOCK
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public void retirerStock(Integer produitId, Double quantite,
                             MouvementStockEntity.TypeMouvement type,
                             String motif) {

        validateQuantite(quantite);

        ProduitEntity produit = getProduitOrThrow(produitId);
        Double stockAvant = getStockReelFromEtat(produitId);

        // ✅ Vérifier stock suffisant
        if (stockAvant < quantite) {
            throw new RuntimeException(
                    String.format("❌ Stock insuffisant pour %s (%s). Disponible: %.2f, Demandé: %.2f",
                            produit.getReferences(), produit.getDesignation(),
                            stockAvant, quantite));
        }

        Double stockApres = stockAvant - quantite;

        // ✅ Update EtatduStock
        etatduStockRepository.retirerDuStock(produitId, quantite);

        // ✅ Créer mouvement
        MouvementStockEntity mouvement = MouvementStockEntity.builder()
                .reference(generateReference())
                .produit(produit)
                .typeMouvement(type)
                .quantite(quantite)
                .quantiteAvant(stockAvant)
                .quantiteApres(stockApres)
                .motif(motif)
                .dateMouvement(LocalDateTime.now())
                .statut(MouvementStockEntity.StatutMouvement.VALIDE)
                .build();

        mouvementStockRepository.save(mouvement);

        log.info("✅ STOCK -{} → Produit {} ({}) | Type: {} | {} → {}",
                quantite, produit.getReferences(), produit.getDesignation(),
                type, stockAvant, stockApres);
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ MOUVEMENTS AVEC DOCUMENT SOURCE
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public MouvementStockEntity creerMouvementEntree(Integer produitId, Double quantite,
                                                     MouvementStockEntity.TypeMouvement type,
                                                     String motif, Integer bonReceptionId,
                                                     Integer retourProduitId, Integer inventaireId) {

        validateQuantite(quantite);

        ProduitEntity produit = getProduitOrThrow(produitId);
        Double stockAvant = getStockReelFromEtat(produitId);
        Double stockApres = stockAvant + quantite;

        // ✅ Update EtatduStock
        int updated = etatduStockRepository.ajouterAuStock(produitId, quantite);
        if (updated == 0) {
            creerEtatStockSiAbsent(produit, quantite);
        }

        // ✅ Créer mouvement
        MouvementStockEntity mouvement = MouvementStockEntity.builder()
                .reference(generateReference())
                .produit(produit)
                .typeMouvement(type)
                .quantite(quantite)
                .quantiteAvant(stockAvant)
                .quantiteApres(stockApres)
                .motif(motif)
                .dateMouvement(LocalDateTime.now())
                .statut(MouvementStockEntity.StatutMouvement.VALIDE)
                .build();

        log.info("✅ MOUVEMENT ENTRÉE: +{} → {} | Type: {}", quantite, produit.getReferences(), type);

        return mouvementStockRepository.save(mouvement);
    }

    @Override
    @Transactional
    public MouvementStockEntity creerMouvementSortie(Integer produitId, Double quantite,
                                                     MouvementStockEntity.TypeMouvement type,
                                                     String motif, Integer bonSortieId,
                                                     Integer retourProduitId) {

        validateQuantite(quantite);

        ProduitEntity produit = getProduitOrThrow(produitId);
        Double stockAvant = getStockReelFromEtat(produitId);

        if (stockAvant < quantite) {
            throw new RuntimeException(
                    String.format("❌ Stock insuffisant pour %s. Disponible: %.2f, Demandé: %.2f",
                            produit.getReferences(), stockAvant, quantite));
        }

        Double stockApres = stockAvant - quantite;

        // ✅ Update EtatduStock
        etatduStockRepository.retirerDuStock(produitId, quantite);

        // ✅ Créer mouvement
        MouvementStockEntity mouvement = MouvementStockEntity.builder()
                .reference(generateReference())
                .produit(produit)
                .typeMouvement(type)
                .quantite(quantite)
                .quantiteAvant(stockAvant)
                .quantiteApres(stockApres)
                .motif(motif)
                .dateMouvement(LocalDateTime.now())
                .statut(MouvementStockEntity.StatutMouvement.VALIDE)
                .build();

        log.info("✅ MOUVEMENT SORTIE: -{} → {} | Type: {}", quantite, produit.getReferences(), type);

        return mouvementStockRepository.save(mouvement);
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ VÉRIFICATIONS
    // ═══════════════════════════════════════════════════════════════

    @Override
    public boolean hasStockSuffisant(Integer produitId, Double quantiteDemandee) {
        Double stockActuel = getStockReelFromEtat(produitId);
        return stockActuel >= quantiteDemandee;
    }

    @Override
    public void verifierStockOuException(Integer produitId, Double quantiteDemandee) {
        if (!hasStockSuffisant(produitId, quantiteDemandee)) {
            ProduitEntity produit = getProduitOrThrow(produitId);
            Double stockActuel = getStockReelFromEtat(produitId);
            throw new RuntimeException(
                    String.format("❌ Stock insuffisant pour %s (%s). Disponible: %.2f, Demandé: %.2f",
                            produit.getReferences(), produit.getDesignation(),
                            stockActuel, quantiteDemandee));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ SYNCHRONISATION STOCK
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public void synchroniserStock(Integer produitId) {
        Double stockCalcule = getStockReel(produitId);

        EtatduStock etat = etatduStockRepository.findByProduitId(produitId)
                .orElseGet(() -> {
                    ProduitEntity produit = getProduitOrThrow(produitId);
                    EtatduStock newEtat = new EtatduStock();
                    newEtat.setProduit(produit);
                    newEtat.setStockMin(0.0);
                    newEtat.setStockMax(1000.0);
                    newEtat.setStockReel(0.0);
                    return newEtat;
                });

        Double ancienStock = etat.getStockReel();
        etat.setStockReel(stockCalcule);
        etatduStockRepository.save(etat);

        log.info("🔄 Sync stock {} : {} → {}", produitId, ancienStock, stockCalcule);
    }

    @Override
    @Transactional
    public void synchroniserTousLesStocks() {
        log.info("🔄 Début synchronisation de tous les stocks...");
        produitRepository.findAll().forEach(produit -> {
            try {
                synchroniserStock(produit.getId());
            } catch (Exception e) {
                log.error("❌ Erreur sync produit {} : {}", produit.getId(), e.getMessage());
            }
        });
        log.info("✅ Synchronisation terminée");
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ ALERTES STOCK
    // ═══════════════════════════════════════════════════════════════

    @Override
    public boolean isStockBas(Integer produitId) {
        return etatduStockRepository.findByProduitId(produitId)
                .map(etat -> etat.getStockReel() <= etat.getStockMin())
                .orElse(false);
    }

    @Override
    public boolean isStockCritique(Integer produitId) {
        return etatduStockRepository.findByProduitId(produitId)
                .map(etat -> etat.getStockReel() <= (etat.getStockMin() / 2))
                .orElse(false);
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ MÉTHODES UTILITAIRES PRIVÉES
    // ══════════════════════════════════════════════════════════════

    private Double getStockReelFromEtat(Integer produitId) {
        return etatduStockRepository.findByProduitId(produitId)
                .map(EtatduStock::getStockReel)
                .orElse(0.0);
    }

    private ProduitEntity getProduitOrThrow(Integer produitId) {
        return produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable : " + produitId));
    }

    private void validateQuantite(Double quantite) {
        if (quantite == null || quantite <= 0) {
            throw new IllegalArgumentException("Quantité invalide : " + quantite);
        }
    }

    private Double safeDouble(Double value) {
        return value != null ? value : 0.0;
    }

    private String generateReference() {
        return "MVT-" + System.currentTimeMillis();
    }

    private void creerEtatStockSiAbsent(ProduitEntity produit, Double quantiteInitiale) {
        if (etatduStockRepository.findByProduitId(produit.getId()).isEmpty()) {
            EtatduStock etat = new EtatduStock();
            etat.setProduit(produit);
            etat.setStockReel(quantiteInitiale);
            etat.setStockMin(0.0);
            etat.setStockMax(1000.0);
            etatduStockRepository.save(etat);
            log.info("📦 EtatduStock créé pour {} avec stock initial {}",
                    produit.getReferences(), quantiteInitiale);
        }
    }
}