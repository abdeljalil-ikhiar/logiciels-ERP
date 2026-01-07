package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.LigneCommandeEntity;
import com.example.AppPfa.Repository.LigneCommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LigneCommandeService implements LigneCommandeManager {

    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;

    @Override
    public LigneCommandeEntity addLigneCommande(LigneCommandeEntity ligneCommandeEntity) {
        // ✅ Valeurs par défaut
        setDefaultValues(ligneCommandeEntity);
        calculerTotalLigne(ligneCommandeEntity);
        return ligneCommandeRepository.save(ligneCommandeEntity);
    }

    @Override
    public LigneCommandeEntity updateLigneCommande(int id, LigneCommandeEntity ligneCommandeEntity) {
        LigneCommandeEntity existing = ligneCommandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LigneCommande non trouvée avec id : " + id));

        existing.setQuantite(ligneCommandeEntity.getQuantite());
        existing.setPrixUnitaire(ligneCommandeEntity.getPrixUnitaire());
        existing.setProduit(ligneCommandeEntity.getProduit());
        existing.setRemisePourcentage(ligneCommandeEntity.getRemisePourcentage()); // ✅ Mise à jour remise

        // ✅ Valeurs par défaut
        setDefaultValues(existing);

        // Recalcul automatique
        calculerTotalLigne(existing);

        return ligneCommandeRepository.save(existing);
    }

    @Override
    public List<LigneCommandeEntity> getLigneCommande() {
        return ligneCommandeRepository.findAll();
    }

    @Override
    public void deleteLigneCommande(int id) {
        ligneCommandeRepository.deleteById(id);
    }

    // ✅ Calcul avec Remise
    @Override
    public void calculerTotalLigne(LigneCommandeEntity ligne) {
        if (ligne.getProduit() != null) {
            double quantite = ligne.getQuantite() != null ? ligne.getQuantite() : 0.0;
            double prixUnitaire = ligne.getPrixUnitaire() != null ? ligne.getPrixUnitaire() : 0.0;

            // HT avant remise
            double htBrut = quantite * prixUnitaire;

            // Appliquer la remise
            Double remise = ligne.getRemisePourcentage() != null ? ligne.getRemisePourcentage() : 0.0;
            if (remise < 0) remise = 0.0;
            if (remise > 100) remise = 100.0;

            double montantRemise = htBrut * remise / 100;
            double totalHT = htBrut - montantRemise;

            // TTC avec TVA
            double tva = ligne.getProduit().getTva() != null ? ligne.getProduit().getTva() : 0.0;
            double totalTTC = totalHT * (1 + tva / 100);

            // Arrondir à 2 décimales
            ligne.setTotalHT(Math.round(totalHT * 100.0) / 100.0);
            ligne.setTotalTTC(Math.round(totalTTC * 100.0) / 100.0);
        }
    }

    // ✅ Méthode pour définir les valeurs par défaut
    private void setDefaultValues(LigneCommandeEntity ligne) {
        if (ligne.getQuantite() == null) {
            ligne.setQuantite(0.0);
        }
        if (ligne.getPrixUnitaire() == null) {
            ligne.setPrixUnitaire(0.0);
        }
        if (ligne.getRemisePourcentage() == null) {
            ligne.setRemisePourcentage(0.0);
        }
        if (ligne.getTotalHT() == null) {
            ligne.setTotalHT(0.0);
        }
        if (ligne.getTotalTTC() == null) {
            ligne.setTotalTTC(0.0);
        }
    }
}