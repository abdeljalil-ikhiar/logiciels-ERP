package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.FournisseurEntity;

import java.util.List;

public interface FournisseurManager {
    FournisseurEntity addFournniseur(FournisseurEntity fournisseurEntity);
    List<FournisseurEntity> getAllFournisseur();
    FournisseurEntity updateFournisseur(int id ,FournisseurEntity fournisseurEntity);
    void deleteFournisseur(int id);
}
