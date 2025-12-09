package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.BonDeLivraisonFournisseurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BonDeLivraisonFournisseurRepository extends JpaRepository<BonDeLivraisonFournisseurEntity, Integer> {

    // ✅ Vérifier si un BL existe pour un BR donné
    boolean existsByBonDeReceptionEntityId(Integer bonReceptionId);

    // ✅ Trouver par ID de bon de réception
    Optional<BonDeLivraisonFournisseurEntity> findByBonDeReceptionEntityId(Integer bonReceptionId);

    // ✅ Trouver par fournisseur
    List<BonDeLivraisonFournisseurEntity> findByFournisseurEntityId(Integer fournisseurId);

    // ✅ Trouver par statut
    List<BonDeLivraisonFournisseurEntity> findByStatut(BonDeLivraisonFournisseurEntity.StatutLivraison statut);

    // ✅ Recherche par numéro
    Optional<BonDeLivraisonFournisseurEntity> findByNumeroLivraison(String numeroLivraison);

    // ✅ Compter par statut
    long countByStatut(BonDeLivraisonFournisseurEntity.StatutLivraison statut);
}