package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.DepotEntity;
import java.util.List;

public interface DepotManager {
    DepotEntity AddDepot(DepotEntity depotEntity);
    DepotEntity UpdateDepot(int id, DepotEntity depotEntity);
    void DeletDepot(int id);
    List<DepotEntity> getAllDepot(); // ✅ حذفت parameter
    DepotEntity getDepotById(int id); // ✅ زدت هذا
}