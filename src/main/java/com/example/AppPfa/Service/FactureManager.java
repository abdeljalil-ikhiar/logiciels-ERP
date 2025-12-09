package com.example.AppPfa.Service;
import com.example.AppPfa.DAO.Entity.FactureEntity;
import java.util.List;
public interface FactureManager {
    FactureEntity addFacture (FactureEntity factureEntity);
    FactureEntity updateFactue(int id,FactureEntity factureEntity);
    List<FactureEntity> getAllFacture();
    void deleteFacture(int id);
}

