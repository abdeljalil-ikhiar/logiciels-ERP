package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.CommandeEntity;
import com.example.AppPfa.DAO.Entity.LigneCommandeEntity;
import com.example.AppPfa.DAO.Entity.LigneDevisEntity;

import java.util.List;

public interface LigneCommandeManager {
    LigneCommandeEntity addLigneCommande(LigneCommandeEntity ligneCommandeEntity);
    LigneCommandeEntity updateLigneCommande(int id, LigneCommandeEntity ligneCommandeEntity);
    List<LigneCommandeEntity> getLigneCommande();
    void deleteLigneCommande(int id);
    void calculerTotalLigne(LigneCommandeEntity ligneCommandeEntity);

}
