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
        existing.setRemisePourcentage(ligneDevisEntity.getRemisePourcentage()); // mise à jour remise

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
            // Calcul HT avant remise
            double totalHT = ligne.getPrixUnitaire() * ligne.getQuantite();

            // Appliquer remise %
            Double remise = ligne.getRemisePourcentage() != null ? ligne.getRemisePourcentage() : 0.0;
            if (remise < 0) remise = 0.0;
            if (remise > 100) remise = 100.0;
            totalHT = totalHT - (totalHT * remise / 100);

            // Calcul TTC avec TVA du produit
            double totalTTC = totalHT * (1 + ligne.getProduit().getTva() / 100);

            // Mettre à jour l'entité
            ligne.setTotalHT(totalHT);
            ligne.setTotalTTC(totalTTC);
        }
    }
}
