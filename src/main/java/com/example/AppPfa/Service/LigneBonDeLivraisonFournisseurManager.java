package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.LigneBonDeLivraisonFournisseurEntity;

import java.util.List;

public interface LigneBonDeLivraisonFournisseurManager {

    LigneBonDeLivraisonFournisseurEntity getLigneBonLivraisonById(Integer id);

    List<LigneBonDeLivraisonFournisseurEntity> getAllLignesBonLivraison();

    List<LigneBonDeLivraisonFournisseurEntity> getLignesByBonLivraison(Integer bonLivraisonId);

    LigneBonDeLivraisonFournisseurEntity addLigneBonLivraison(Integer bonLivraisonId, Integer ligneBonReceptionId);

    void deleteLigneBonLivraison(Integer id);
}