package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.LigneBonLivraisonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneBonLivraisonRepository extends JpaRepository<LigneBonLivraisonEntity, Integer> {
    List<LigneBonLivraisonEntity> findByBonLivraisonId(Integer bonLivraisonId);
    List<LigneBonLivraisonEntity> findByLigneBonSortieId(Integer ligneBonSortieId);
    List<LigneBonLivraisonEntity> findByLigneCommandeId(Integer ligneCommandeId);
}