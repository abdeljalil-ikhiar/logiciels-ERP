package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.LigneFactureFournisseurEntity;

import java.util.List;

public interface LigneFactureFournisseurManager {

    LigneFactureFournisseurEntity getLigneFactureFournisseurById(Integer id);

    List<LigneFactureFournisseurEntity> getAllLignesFactureFournisseur();

    List<LigneFactureFournisseurEntity> getLignesByFactureFournisseurId(Integer factureId);

    LigneFactureFournisseurEntity addLigneFactureFournisseur(Integer factureId, Integer ligneBonLivraisonFournisseurId);

    void deleteLigneFactureFournisseur(Integer id);
}