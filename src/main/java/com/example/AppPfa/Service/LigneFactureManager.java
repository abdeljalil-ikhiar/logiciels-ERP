package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.LigneFactureEntity;
import java.util.List;

public interface LigneFactureManager {

    LigneFactureEntity getLigneFactureById(Integer id);

    List<LigneFactureEntity> getAllLignesFacture();

    List<LigneFactureEntity> getLignesByFactureId(Integer factureId);

    LigneFactureEntity addLigneFacture(Integer factureId, Integer ligneBonLivraisonId);

    void deleteLigneFacture(Integer id);
}
