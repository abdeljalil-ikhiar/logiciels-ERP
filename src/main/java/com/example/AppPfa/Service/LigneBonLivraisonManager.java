package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.LigneBonLivraisonEntity;

import java.util.List;

public interface LigneBonLivraisonManager {

    /**
     * Récupérer une ligne de bon de livraison par ID
     */
    LigneBonLivraisonEntity getLigneBonLivraisonById(Integer id);

    /**
     * Récupérer toutes les lignes de bon de livraison
     */
    List<LigneBonLivraisonEntity> getLigneBonLivraison();

    /**
     * Récupérer les lignes d'un bon de livraison spécifique
     */
    List<LigneBonLivraisonEntity> getLigneBonLivraisonByBonLivraison(Integer bonLivraisonId);

    /**
     * Supprimer une ligne de bon de livraison (pour correction uniquement)
     */
    void deleteLigneBonLivraison(Integer id);

    public LigneBonLivraisonEntity addLigneBonLivraison(Integer bonLivraisonId, Integer ligneBonSortieId);


}