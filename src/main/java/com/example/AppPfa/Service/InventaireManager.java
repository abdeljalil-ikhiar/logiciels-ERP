package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.InventaireEntity;
import java.util.List;

public interface InventaireManager {


    InventaireEntity addInventaire(InventaireEntity inventaire);


    InventaireEntity updateInventaire(Integer id, InventaireEntity inventaire);


    List<InventaireEntity> getAllInventaires();


    void deleteInventaire(Integer id);

    InventaireEntity getInventaireById(Integer id);
}
