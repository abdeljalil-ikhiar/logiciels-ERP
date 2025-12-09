package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.LigneBonDeReceptionEntities;

import java.util.List;

public interface LigneBonDeReceptionManager {

    LigneBonDeReceptionEntities addLigne(LigneBonDeReceptionEntities ligne);
    LigneBonDeReceptionEntities updateLigne(int id, LigneBonDeReceptionEntities ligne);
    List<LigneBonDeReceptionEntities> getAllLignes();
    LigneBonDeReceptionEntities getLigneById(int id);
    void deleteLigne(int id);
}
