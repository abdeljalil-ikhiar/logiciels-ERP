package com.example.AppPfa.Service;
import com.example.AppPfa.DAO.Entity.CommandeAchatsEntity;
import java.util.List;
public interface CommandeAchatsManager {
    CommandeAchatsEntity addCommandeAchats(CommandeAchatsEntity commandeAchatsEntity);
    List<CommandeAchatsEntity> getCommandeAchats();
    CommandeAchatsEntity updateCommandeAchats(int id, CommandeAchatsEntity commandeAchatsEntity);
    void deleteCommandeAchats(int id);
}