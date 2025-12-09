package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.BonDeReceptionEntity;
import java.util.List;

public interface BonDeReceptionManager {
    BonDeReceptionEntity addBonReception(BonDeReceptionEntity bonDeReceptionEntity);
    BonDeReceptionEntity updateBonReception(int id, BonDeReceptionEntity bonDeReceptionEntity);
    List<BonDeReceptionEntity> getAllBonReception();
    BonDeReceptionEntity getBonReceptionById(int id);
    void deleteBonReception(int id);
}
