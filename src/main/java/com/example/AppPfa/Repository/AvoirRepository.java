package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.AvoirEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvoirRepository extends JpaRepository<AvoirEntity, Integer> {

    // ✅ Chercher par numéro
    Optional<AvoirEntity> findByNumeroAvoir(String numeroAvoir);

    // ✅ Avoirs par client
    List<AvoirEntity> findByClientIdOrderByDateAvoirDesc(Integer clientId);

    // ✅ Avoirs par fournisseur
    List<AvoirEntity> findByFournisseurIdOrderByDateAvoirDesc(Integer fournisseurId);

    // ✅ Avoirs par type
    List<AvoirEntity> findByTypeAvoirOrderByDateAvoirDesc(AvoirEntity.TypeAvoir typeAvoir);

    // ✅ Avoirs par statut
    List<AvoirEntity> findByStatutOrderByDateAvoirDesc(AvoirEntity.StatutAvoir statut);

    // ✅ Avoir par retour produit
    Optional<AvoirEntity> findByRetourProduitId(Integer retourProduitId);

    // ✅ Avoirs par facture
    List<AvoirEntity> findByFactureId(Integer factureId);

    // ✅ Avoirs par facture fournisseur
    List<AvoirEntity> findByFactureFournisseurId(Integer factureFournisseurId);

    // ✅ Avoirs entre deux dates
    @Query("SELECT a FROM AvoirEntity a " +
            "WHERE a.dateAvoir BETWEEN :debut AND :fin " +
            "ORDER BY a.dateAvoir DESC")
    List<AvoirEntity> findByDateAvoirBetween(
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin
    );

    // ✅ Avoirs clients disponibles pour UN client donné
    @Query("SELECT a FROM AvoirEntity a " +
            "WHERE a.typeAvoir = 'AVOIR_CLIENT' " +
            "AND a.statut = 'VALIDE' " +
            "AND a.client.id = :clientId " +
            "ORDER BY a.dateAvoir DESC")
    List<AvoirEntity> findAvoirsClientDisponibles(@Param("clientId") Integer clientId);

    // ✅ Somme avoirs disponibles par client
    @Query("SELECT COALESCE(SUM(a.totalTTC), 0) FROM AvoirEntity a " +
            "WHERE a.client.id = :clientId " +
            "AND a.statut = 'VALIDE'")
    Double sumAvoirsDisponiblesByClient(@Param("clientId") Integer clientId);

    // ✅ Générer numéro avoir (pour ton generateNumeroAvoir)
    @Query("SELECT MAX(a.id) FROM AvoirEntity a")
    Integer findMaxId();
}