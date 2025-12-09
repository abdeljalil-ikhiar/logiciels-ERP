package com.example.AppPfa.Service;
import com.example.AppPfa.DAO.Entity.LigneBonDeReceptionEntities;
import com.example.AppPfa.Repository.LigneBon_de_receptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LigneBonDeReceptionService {

    @Autowired
    private LigneBon_de_receptionRepository ligneRepo;

    public LigneBonDeReceptionEntities addLigne(LigneBonDeReceptionEntities ligne) {
        return ligneRepo.save(ligne);
    }

    public LigneBonDeReceptionEntities updateLigne(int id, LigneBonDeReceptionEntities ligne) {
        LigneBonDeReceptionEntities existing = ligneRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne introuvable : " + id));

        existing.setQtereception(ligne.getQtereception());
        existing.setBonDeReceptionEntity(ligne.getBonDeReceptionEntity());
        existing.setLigneCommandeAchatsEntity(ligne.getLigneCommandeAchatsEntity());

        return ligneRepo.save(existing);
    }

    public List<LigneBonDeReceptionEntities> getAllLignes() {
        return ligneRepo.findAll();
    }

    public List<LigneBonDeReceptionEntities> getLignesByBonId(Integer bonId) {
        return ligneRepo.findByBonDeReceptionEntityId(bonId);
    }

    public void deleteLigne(int id) {
        if (!ligneRepo.existsById(id)) {
            throw new RuntimeException("Ligne introuvable : " + id);
        }
        ligneRepo.deleteById(id);
    }
}
