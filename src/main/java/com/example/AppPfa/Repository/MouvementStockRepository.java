package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.MouvementStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStockEntity, Integer> {

    // ✅ Chercher par référence
    Optional<MouvementStockEntity> findByReference(String reference);

    // ✅ Mouvements par produit
    List<MouvementStockEntity> findByProduitIdOrderByDateMouvementDesc(Integer produitId);

    // ✅ Mouvements par type
    List<MouvementStockEntity> findByTypeMouvementOrderByDateMouvementDesc(MouvementStockEntity.TypeMouvement type);

    // ✅ Mouvements par statut
    List<MouvementStockEntity> findByStatutOrderByDateMouvementDesc(MouvementStockEntity.StatutMouvement statut);

    // ✅ Mouvements entre deux dates
    @Query("SELECT m FROM MouvementStockEntity m WHERE m.dateMouvement BETWEEN :debut AND :fin ORDER BY m.dateMouvement DESC")
    List<MouvementStockEntity> findByDateMouvementBetween(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin
    );

    // ✅ Mouvements par produit et type
    List<MouvementStockEntity> findByProduitIdAndTypeMouvementOrderByDateMouvementDesc(
            Integer produitId,
            MouvementStockEntity.TypeMouvement type
    );

    // ✅ Somme des entrées par produit
    @Query("SELECT COALESCE(SUM(m.quantite), 0) FROM MouvementStockEntity m " +
            "WHERE m.produit.id = :produitId " +
            "AND m.typeMouvement IN ('ENTREE_ACHAT', 'RETOUR_CLIENT', 'AJUSTEMENT_POSITIF', 'ENTREE_INITIALE') " +
            "AND m.statut = 'VALIDE'")
    Double sumEntreesByProduit(@Param("produitId") Integer produitId);

    // ✅ Somme des sorties par produit
    @Query("SELECT COALESCE(SUM(m.quantite), 0) FROM MouvementStockEntity m " +
            "WHERE m.produit.id = :produitId " +
            "AND m.typeMouvement IN ('SORTIE_VENTE', 'RETOUR_FOURNISSEUR', 'AJUSTEMENT_NEGATIF', 'PERTE') " +
            "AND m.statut = 'VALIDE'")
    Double sumSortiesByProduit(@Param("produitId") Integer produitId);

    // ✅ Mouvements par bon de réception
    List<MouvementStockEntity> findByBonDeReceptionId(Integer bonReceptionId);

    // ✅ Mouvements par bon de sortie
    List<MouvementStockEntity> findByBonSortieId(Integer bonSortieId);

    // ✅ Mouvements par retour produit
    List<MouvementStockEntity> findByRetourProduitId(Integer retourProduitId);

    // ✅ Mouvements par inventaire
    List<MouvementStockEntity> findByInventaireId(Integer inventaireId);

    // ✅ Derniers mouvements (pagination)
    @Query("SELECT m FROM MouvementStockEntity m ORDER BY m.dateMouvement DESC")
    List<MouvementStockEntity> findLastMovements();

    // ✅ Count mouvements par type
    @Query("SELECT m.typeMouvement, COUNT(m) FROM MouvementStockEntity m GROUP BY m.typeMouvement")
    List<Object[]> countByTypeMouvement();
}