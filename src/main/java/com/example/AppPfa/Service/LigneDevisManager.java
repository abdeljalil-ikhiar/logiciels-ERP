package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.DevisEntity;
import com.example.AppPfa.DAO.Entity.LigneDevisEntity;

import java.util.List;

public interface LigneDevisManager {
    LigneDevisEntity addLigneDevis(LigneDevisEntity ligneDevisEntity);
    LigneDevisEntity updateLigneDevis(int id,LigneDevisEntity ligneDevisEntity);
    List<LigneDevisEntity> getAllLigneDevis();
    void deleteLigneDevis(int id);
     void calculerTotalLigne(LigneDevisEntity ligne);
}
