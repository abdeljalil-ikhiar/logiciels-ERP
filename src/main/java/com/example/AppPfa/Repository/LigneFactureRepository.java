package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.LigneFactureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneFactureRepository extends JpaRepository<LigneFactureEntity, Integer> {

    List<LigneFactureEntity> findByFactureId(Integer factureId);

    // ✅ NOUVEAU: Supprimer les lignes par facture ID
    @Modifying
    @Query("DELETE FROM LigneFactureEntity l WHERE l.facture.id = :factureId")
    void deleteByFactureId(@Param("factureId") Integer factureId);
}