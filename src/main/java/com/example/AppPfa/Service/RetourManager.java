package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.RetourProduitEntity;
import java.util.List;
import java.util.Map;

public interface RetourManager {

    // ✅ On passe les données brutes (IDs + Liste des lignes en Map)
    RetourProduitEntity creerRetourClient(Integer clientId, Integer bonLivraisonId, List<Map<String, Object>> lignesData, String motif);

    RetourProduitEntity creerRetourFournisseur(Integer fournisseurId, Integer bonReceptionId, List<Map<String, Object>> lignesData, String motif);

    RetourProduitEntity validerRetour(Integer retourId, boolean genererAvoir);
    RetourProduitEntity annulerRetour(Integer retourId);

    List<RetourProduitEntity> getAllRetours();
    RetourProduitEntity getRetourById(Integer id);
}