package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.LigneRetourEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneRetourRepository extends JpaRepository<LigneRetourEntity, Integer> {

    List<LigneRetourEntity> findByRetourProduitId(Integer retourProduitId);

    List<LigneRetourEntity> findByProduitId(Integer produitId);

    // ✅ Retours clients validés (BON ÉTAT = réintégré au stock)
    @Query("SELECT COALESCE(SUM(l.quantiteRetournee), 0) FROM LigneRetourEntity l " +
            "WHERE l.produit.id = :produitId " +
            "AND l.retourProduit.typeRetour = 'RETOUR_CLIENT' " +
            "AND l.etatProduit = 'BON_ETAT' " +
            "AND l.retourProduit.statut IN ('VALIDE', 'AVOIR_GENERE')")
    Double sumRetoursClientsValides(@Param("produitId") Integer produitId);

    // ✅ Retours fournisseur validés (= sortie du stock)
    @Query("SELECT COALESCE(SUM(l.quantiteRetournee), 0) FROM LigneRetourEntity l " +
            "WHERE l.produit.id = :produitId " +
            "AND l.retourProduit.typeRetour = 'RETOUR_FOURNISSEUR' " +
            "AND l.retourProduit.statut IN ('VALIDE', 'AVOIR_GENERE')")
    Double sumRetoursFournisseurValides(@Param("produitId") Integer produitId);

    // ✅ Somme générique (pour compatibilité)
    @Query("SELECT COALESCE(SUM(l.quantiteRetournee), 0) FROM LigneRetourEntity l " +
            "WHERE l.produit.id = :produitId " +
            "AND l.retourProduit.statut IN ('VALIDE', 'AVOIR_GENERE')")
    Double sumQuantiteRetourneeByProduit(@Param("produitId") Integer produitId);

    List<LigneRetourEntity> findByEtatProduit(LigneRetourEntity.EtatProduit etatProduit);

    List<LigneRetourEntity> findByActionRetour(LigneRetourEntity.ActionRetour actionRetour);
}