package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.AvoirEntity;
import com.example.AppPfa.DAO.Entity.RetourProduitEntity;

import java.util.List;

public interface AvoirManager {

    // ═══════════════════════════════════════════════════════════════
    // ✅ CRÉATION
    // ═══════════════════════════════════════════════════════════════

    AvoirEntity creerAvoirDepuisRetour(RetourProduitEntity retour);

    AvoirEntity creerAvoirManuel(Integer clientId, Double montant, String commentaire);

    // ═══════════════════════════════════════════════════════════════
    // ✅ ACTIONS
    // ═══════════════════════════════════════════════════════════════

    AvoirEntity utiliserAvoir(Integer avoirId);

    AvoirEntity annulerAvoir(Integer avoirId);

    // ═══════════════════════════════════════════════════════════════
    // ✅ GETTERS
    // ═══════════════════════════════════════════════════════════════

    List<AvoirEntity> getAllAvoirs();

    AvoirEntity getAvoirById(Integer id);

    List<AvoirEntity> getAvoirsByClient(Integer clientId);

    List<AvoirEntity> getAvoirsByFournisseur(Integer fournisseurId);

    List<AvoirEntity> getAvoirsDisponibles(Integer clientId);

    Double getSoldeAvoirsClient(Integer clientId);
}