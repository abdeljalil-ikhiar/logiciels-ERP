package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.FactureFournisseurEntity;

import java.util.List;

public interface FactureFournisseurManager {

    FactureFournisseurEntity addFactureFournisseur(FactureFournisseurEntity factureFournisseurEntity);

    FactureFournisseurEntity updateFactureFournisseur(int id, FactureFournisseurEntity factureFournisseurEntity);

    List<FactureFournisseurEntity> getAllFactureFournisseur();

    FactureFournisseurEntity getFactureFournisseurById(Integer id);

    void deleteFactureFournisseur(int id);

    FactureFournisseurEntity recalculerFactureFournisseur(Integer factureId);

    FactureFournisseurEntity recalculerFactureFournisseurParBonLivraison(Integer bonLivraisonId);
}