package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.LigneFactureFournisseurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneFactureFournisseurRepository extends JpaRepository<LigneFactureFournisseurEntity, Integer> {

    List<LigneFactureFournisseurEntity> findByFactureFournisseurEntityId(Integer factureId);

    @Modifying
    @Query("DELETE FROM LigneFactureFournisseurEntity l WHERE l.factureFournisseurEntity.id = :factureId")
    void deleteByFactureFournisseurEntityId(@Param("factureId") Integer factureId);
}