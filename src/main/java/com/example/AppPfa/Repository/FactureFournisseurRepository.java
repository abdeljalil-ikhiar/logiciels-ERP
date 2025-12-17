package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.FactureFournisseurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FactureFournisseurRepository extends JpaRepository<FactureFournisseurEntity, Integer> {

    Optional<FactureFournisseurEntity> findByNumeroFacture(String numeroFacture);

    boolean existsByNumeroFacture(String numeroFacture);

    boolean existsByBonDeLivraisonFournisseurEntity_Id(Integer bonLivraisonId);

    Optional<FactureFournisseurEntity> findByBonDeLivraisonFournisseurEntity_Id(Integer bonLivraisonId);

    List<FactureFournisseurEntity> findByDateFactureBetween(LocalDate debut, LocalDate fin);

    @Query("SELECT f FROM FactureFournisseurEntity f " +
            "WHERE f.bonDeLivraisonFournisseurEntity.fournisseurEntity.id = :fournisseurId")
    List<FactureFournisseurEntity> findByFournisseurId(@Param("fournisseurId") Integer fournisseurId);

    long countByNumeroFactureStartingWith(String prefix);
}