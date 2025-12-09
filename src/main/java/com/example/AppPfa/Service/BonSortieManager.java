package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.BonSortieEntity;

import java.util.List;

public interface BonSortieManager {
    BonSortieEntity addBonSortie(BonSortieEntity bonSortieEntity);
    BonSortieEntity updateBonSortie(int id,BonSortieEntity bonSortieEntity);
    List<BonSortieEntity> getBonSortie();
    void deleteBonSortie(int id);
}
