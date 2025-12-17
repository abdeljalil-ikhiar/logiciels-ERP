package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.LigneInventaireEntity;

import java.util.List;

public interface LigneInventaireManager {
    LigneInventaireEntity addLigneInventaire(LigneInventaireEntity ligneInventaireEntity);
    LigneInventaireEntity updateLigneInventaire(int id,LigneInventaireEntity ligneInventaireEntity);
    List<LigneInventaireEntity> getAllLigneInventaire();
    void deleteLigneInventaire(Integer id);
}
