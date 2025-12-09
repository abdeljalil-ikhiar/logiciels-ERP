package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.CommandeEntity;

import java.util.List;

public interface CommandeManager {
    CommandeEntity addCommande(CommandeEntity commandeEntity);
    List<CommandeEntity> getCommande();
    CommandeEntity updateCommande(int id,CommandeEntity commandeEntity);
    void deleteCommande(int id);

}
