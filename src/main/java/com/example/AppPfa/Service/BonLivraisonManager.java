package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.BonLivraisonEntity;

import java.util.List;

public interface BonLivraisonManager {

    /**
     * Créer un bon de livraison à partir d'un bon de sortie
     */
    BonLivraisonEntity creerBonLivraison(Integer idBonSortie);

    /**
     * Récupérer un bon de livraison par ID
     */
    BonLivraisonEntity getBonLivraison(Integer id);

    /**
     * Récupérer le bon de livraison d'un bon de sortie
     */
    BonLivraisonEntity getBonLivraisonByBonSortie(Integer idBonSortie);

    /**
     * Récupérer tous les bons de livraison d'une commande
     */
    List<BonLivraisonEntity> getBonLivraisonByCommande(Integer idCommande);

    /**
     * Récupérer tous les bons de livraison
     */
    List<BonLivraisonEntity> getAllBonLivraison();

    /**
     * Supprimer un bon de livraison
     */
    void deleteBonLivraison(Integer id);
}