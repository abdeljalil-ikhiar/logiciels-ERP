package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.ProduitEntity;
import java.util.List;

public interface ProduitManager {
    ProduitEntity addProduit(ProduitEntity produitEntity);
    ProduitEntity updateProduit(int id, ProduitEntity produitEntity);
    void deleteProduit(int id);
    ProduitEntity getProduitById(int id);
    List<ProduitEntity> getAllProduit();
    List<ProduitEntity> getProduitsByCategorieId(int categorieId);
}