package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.FactureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FactureRepository extends JpaRepository<FactureEntity, Integer> {

    // Vérifier si une facture existe pour un bon de livraison
    boolean existsByBonLivraison_Id(Integer bonLivraisonId);

    // ✅ NOUVEAU: Trouver une facture par son bon de livraison
    Optional<FactureEntity> findByBonLivraison_Id(Integer bonLivraisonId);

    // Compter les factures par préfixe de numéro
    long countByNumeroFactureStartingWith(String prefix);
}