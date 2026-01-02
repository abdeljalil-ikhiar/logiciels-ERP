package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.RetourProduitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RetourProduitRepository extends JpaRepository<RetourProduitEntity, Integer> {

    // ✅ Chercher par numéro
    Optional<RetourProduitEntity> findByNumeroRetour(String numeroRetour);

    // ✅ Retours par client
    List<RetourProduitEntity> findByClientIdOrderByDateRetourDesc(Integer clientId);

    // ✅ Retours par fournisseur
    List<RetourProduitEntity> findByFournisseurIdOrderByDateRetourDesc(Integer fournisseurId);

    // ✅ Retours par type
    List<RetourProduitEntity> findByTypeRetourOrderByDateRetourDesc(RetourProduitEntity.TypeRetour typeRetour);

    // ✅ Retours par statut
    List<RetourProduitEntity> findByStatutOrderByDateRetourDesc(RetourProduitEntity.StatutRetour statut);

    // ✅ Retours par bon de livraison
    List<RetourProduitEntity> findByBonLivraisonId(Integer bonLivraisonId);

    // ✅ Retours par bon de réception
    List<RetourProduitEntity> findByBonDeReceptionId(Integer bonReceptionId);

    // ✅ Retours entre deux dates
    @Query("SELECT r FROM RetourProduitEntity r WHERE r.dateRetour BETWEEN :debut AND :fin ORDER BY r.dateRetour DESC")
    List<RetourProduitEntity> findByDateRetourBetween(
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin
    );

    // ✅ Retours clients en attente
    @Query("SELECT r FROM RetourProduitEntity r WHERE r.typeRetour = 'RETOUR_CLIENT' AND r.statut = 'EN_ATTENTE'")
    List<RetourProduitEntity> findRetoursClientEnAttente();

    // ✅ Retours fournisseur en attente
    @Query("SELECT r FROM RetourProduitEntity r WHERE r.typeRetour = 'RETOUR_FOURNISSEUR' AND r.statut = 'EN_ATTENTE'")
    List<RetourProduitEntity> findRetoursFournisseurEnAttente();

    // ✅ Générer numéro retour
    @Query("SELECT MAX(r.id) FROM RetourProduitEntity r")
    Integer findMaxId();
}