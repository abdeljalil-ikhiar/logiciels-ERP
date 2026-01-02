package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.EtatduStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface EtatduStockRepository extends JpaRepository<EtatduStock, Integer> {

    @Query("SELECT e FROM EtatduStock e WHERE e.produit.id = :produitId")
    Optional<EtatduStock> findByProduitId(@Param("produitId") Integer produitId);

    @Modifying
    @Transactional
    @Query("UPDATE EtatduStock e SET e.stockReel = e.stockReel + :quantite WHERE e.produit.id = :produitId")
    int ajouterAuStock(@Param("produitId") Integer produitId, @Param("quantite") Double quantite);

    @Modifying
    @Transactional
    @Query("UPDATE EtatduStock e SET e.stockReel = e.stockReel - :quantite WHERE e.produit.id = :produitId")
    int retirerDuStock(@Param("produitId") Integer produitId, @Param("quantite") Double quantite);
}