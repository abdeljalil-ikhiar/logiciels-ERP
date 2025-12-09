package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.LigneBonSortieEntity;

import java.util.List;

public interface LigneBonSortieManager {
    LigneBonSortieEntity addLigneBonSortie(LigneBonSortieEntity ligneBonSortieEntity);
    LigneBonSortieEntity updateLigneBonSortie(int id,LigneBonSortieEntity ligneBonSortieEntity);
    List<LigneBonSortieEntity> getAllLigneBonSortie();
    void deleteLigneBonSortie(int id);
}
