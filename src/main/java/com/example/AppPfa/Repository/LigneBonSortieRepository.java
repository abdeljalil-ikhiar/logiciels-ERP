package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.LigneBonSortieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LigneBonSortieRepository extends JpaRepository<LigneBonSortieEntity, Integer> {

    Optional<LigneBonSortieEntity> findByProduitEchangeIdAndBonSortieId(int produitId, int bonSortieId);

    /**
     * Compte le nombre de lignes de bon de sortie pour un bon de sortie donné.
     * Utilisé pour vérifier si un bon de sortie est vide après suppression de lignes.
     */
    @Query("SELECT COUNT(lbs) FROM LigneBonSortieEntity lbs WHERE lbs.bonSortie.id = :bonSortieId")
    Long countByBonSortieId(@Param("bonSortieId") Integer bonSortieId);
}