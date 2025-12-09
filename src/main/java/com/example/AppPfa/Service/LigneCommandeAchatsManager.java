// LigneCommandeAchatsManager.java
package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.LigneCommandeAchatsEntity;
import java.util.List;

public interface LigneCommandeAchatsManager {
    LigneCommandeAchatsEntity addLigneCommandeAchats(LigneCommandeAchatsEntity ligneCommandeAchatsEntity);
    List<LigneCommandeAchatsEntity> getLigneCommandeAchats();
    List<LigneCommandeAchatsEntity> getLignesByCommandeAchatsId(int commandeAchatsId);
    LigneCommandeAchatsEntity updateLigneCommandeAchats(int id, LigneCommandeAchatsEntity ligneCommandeAchatsEntity);
    void deleteLigneCommandeAchats(int id);
}