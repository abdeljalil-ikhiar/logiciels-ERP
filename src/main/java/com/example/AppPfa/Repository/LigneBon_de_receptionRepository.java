package com.example.AppPfa.Repository;
import com.example.AppPfa.DAO.Entity.LigneBonDeReceptionEntities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneBon_de_receptionRepository extends JpaRepository<LigneBonDeReceptionEntities,Integer> {
    List<LigneBonDeReceptionEntities> findByBonDeReceptionEntityId(Integer bonId);
    // LigneBon_de_receptionRepository.java
    @Query("SELECT COALESCE(SUM(CAST(l.qtereception AS double)), 0) FROM LigneBonDeReceptionEntities l " +
            "WHERE l.ligneCommandeAchatsEntity.produit.id = :produitId")
    Double sumQteReceptionByProduit(@Param("produitId") Integer produitId);
}
