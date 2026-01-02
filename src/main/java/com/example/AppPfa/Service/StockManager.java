package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.MouvementStockEntity;

public interface StockManager {

    // ═══════════════════════════════════════════════════════════════
    // ✅ CALCUL STOCK
    // ═══════════════════════════════════════════════════════════════

    Double getStockReel(Integer produitId);

    Double calculerStockFinal(Integer produitId);

    // ═══════════════════════════════════════════════════════════════
    // ✅ MOUVEMENTS STOCK
    // ═══════════════════════════════════════════════════════════════

    void ajouterStock(Integer produitId, Double quantite,
                      MouvementStockEntity.TypeMouvement type, String motif);

    void retirerStock(Integer produitId, Double quantite,
                      MouvementStockEntity.TypeMouvement type, String motif);

    MouvementStockEntity creerMouvementEntree(Integer produitId, Double quantite,
                                              MouvementStockEntity.TypeMouvement type,
                                              String motif, Integer bonReceptionId,
                                              Integer retourProduitId, Integer inventaireId);

    MouvementStockEntity creerMouvementSortie(Integer produitId, Double quantite,
                                              MouvementStockEntity.TypeMouvement type,
                                              String motif, Integer bonSortieId,
                                              Integer retourProduitId);

    // ═══════════════════════════════════════════════════════════════
    // ✅ VÉRIFICATIONS
    // ═══════════════════════════════════════════════════════════════

    boolean hasStockSuffisant(Integer produitId, Double quantiteDemandee);

    void verifierStockOuException(Integer produitId, Double quantiteDemandee);

    // ═══════════════════════════════════════════════════════════════
    // ✅ SYNCHRONISATION
    // ═══════════════════════════════════════════════════════════════

    void synchroniserStock(Integer produitId);

    void synchroniserTousLesStocks();

    // ═══════════════════════════════════════════════════════════════
    // ✅ ALERTES
    // ═══════════════════════════════════════════════════════════════

    boolean isStockBas(Integer produitId);

    boolean isStockCritique(Integer produitId);
}