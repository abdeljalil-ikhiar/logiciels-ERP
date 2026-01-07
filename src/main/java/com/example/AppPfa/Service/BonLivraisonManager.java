package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.BonLivraisonEntity;

import java.util.List;

public interface BonLivraisonManager {

    BonLivraisonEntity creerBonLivraison(Integer idBonSortie);

    BonLivraisonEntity recalculerBonLivraison(Integer idBonSortie);

    BonLivraisonEntity getBonLivraison(Integer id);

    BonLivraisonEntity getBonLivraisonByBonSortie(Integer idBonSortie);

    List<BonLivraisonEntity> getBonLivraisonByCommande(Integer idCommande);

    List<BonLivraisonEntity> getAllBonLivraison();

    void deleteBonLivraison(Integer id);
}