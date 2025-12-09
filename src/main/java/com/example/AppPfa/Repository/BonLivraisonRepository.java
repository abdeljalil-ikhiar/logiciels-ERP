package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.BonLivraisonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BonLivraisonRepository extends JpaRepository<BonLivraisonEntity, Integer> {
    Optional<BonLivraisonEntity> findByBonSortieId(Integer bonSortieId);
    List<BonLivraisonEntity> findByBonSortieCommandeEntityId(Integer commandeId);
}