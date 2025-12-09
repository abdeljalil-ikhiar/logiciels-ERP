package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.LigneDevisEntity;
import com.example.AppPfa.Repository.LigneDevisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LigneDevisService implements LigneDevisManager {

    @Autowired
    private LigneDevisRepository ligneDevisRepository;

    @Override
    public LigneDevisEntity addLigneDevis(LigneDevisEntity ligneDevisEntity) {
        calculerTotalLigne(ligneDevisEntity);
        return ligneDevisRepository.save(ligneDevisEntity);
    }

    @Override
    public LigneDevisEntity updateLigneDevis(int id, LigneDevisEntity ligneDevisEntity) {
        LigneDevisEntity existing = ligneDevisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LigneDevis non trouvée avec id : " + id));

        existing.setProduit(ligneDevisEntity.getProduit());
        existing.setQuantite(ligneDevisEntity.getQuantite());
        existing.setPrixUnitaire(ligneDevisEntity.getPrixUnitaire());

        // recalcul automatique
        calculerTotalLigne(existing);

        return ligneDevisRepository.save(existing);
    }

    @Override
    public List<LigneDevisEntity> getAllLigneDevis() {
        return ligneDevisRepository.findAll();
    }

    @Override
    public void deleteLigneDevis(int id) {
        ligneDevisRepository.deleteById(id);
    }

    @Override
    public void calculerTotalLigne(LigneDevisEntity ligne) {
        if (ligne.getProduit() != null) {
            double totalHT = ligne.getPrixUnitaire() * ligne.getQuantite();
            double totalTTC = totalHT * (1 + ligne.getProduit().getTva() / 100);
            ligne.setTotalHT(totalHT);
            ligne.setTotalTTC(totalTTC);
        }
    }
}
