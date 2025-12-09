// LigneCommandeAchatsService.java
package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.*;
import com.example.AppPfa.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LigneCommandeAchatsService implements LigneCommandeAchatsManager {

    @Autowired
    private LigneCommandeAchatsRepository ligneCommandeAchatsRepository;

    @Autowired
    private CommandeAchatsRepository commandeAchatsRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Override
    public LigneCommandeAchatsEntity addLigneCommandeAchats(LigneCommandeAchatsEntity ligneCommandeAchatsEntity) {
        // ✅ Charger le produit depuis la BDD
        if (ligneCommandeAchatsEntity.getProduit() != null &&
                ligneCommandeAchatsEntity.getProduit().getId() != null) {
            ProduitEntity produit = produitRepository
                    .findById(ligneCommandeAchatsEntity.getProduit().getId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
            ligneCommandeAchatsEntity.setProduit(produit);
        }

        // ✅ Charger la commande achats depuis la BDD
        if (ligneCommandeAchatsEntity.getCommandeAchatsEntity() != null &&
                ligneCommandeAchatsEntity.getCommandeAchatsEntity().getId() != null) {
            CommandeAchatsEntity commande = commandeAchatsRepository
                    .findById(ligneCommandeAchatsEntity.getCommandeAchatsEntity().getId())
                    .orElseThrow(() -> new RuntimeException("Commande d'achats non trouvée"));
            ligneCommandeAchatsEntity.setCommandeAchatsEntity(commande);
        }

        // ✅ Calcul des totaux HT / TTC
        calculerTotauxLigne(ligneCommandeAchatsEntity);

        // ✅ Sauvegarde de la ligne
        LigneCommandeAchatsEntity savedLigne = ligneCommandeAchatsRepository.save(ligneCommandeAchatsEntity);

        // ✅ Recalculer les totaux de la commande parente
        if (savedLigne.getCommandeAchatsEntity() != null) {
            recalculerTotauxCommande(savedLigne.getCommandeAchatsEntity().getId());
        }

        return savedLigne;
    }

    @Override
    public List<LigneCommandeAchatsEntity> getLigneCommandeAchats() {
        return ligneCommandeAchatsRepository.findAll();
    }

    @Override
    public List<LigneCommandeAchatsEntity> getLignesByCommandeAchatsId(int commandeAchatsId) {
        return ligneCommandeAchatsRepository.findByCommandeAchatsEntityId(commandeAchatsId);
    }

    @Override
    public LigneCommandeAchatsEntity updateLigneCommandeAchats(int id, LigneCommandeAchatsEntity ligneCommandeAchatsEntity) {
        LigneCommandeAchatsEntity existing = ligneCommandeAchatsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne de commande achats non trouvée"));

        // ✅ Mise à jour du produit
        if (ligneCommandeAchatsEntity.getProduit() != null &&
                ligneCommandeAchatsEntity.getProduit().getId() != null) {
            ProduitEntity produit = produitRepository
                    .findById(ligneCommandeAchatsEntity.getProduit().getId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
            existing.setProduit(produit);
        }

        // ✅ Mise à jour quantité et prix
        existing.setQuantite(ligneCommandeAchatsEntity.getQuantite());
        existing.setPrixUnitaire(ligneCommandeAchatsEntity.getPrixUnitaire());

        // ✅ Recalcul des totaux de la ligne
        calculerTotauxLigne(existing);

        // ✅ Sauvegarde
        LigneCommandeAchatsEntity savedLigne = ligneCommandeAchatsRepository.save(existing);

        // ✅ Recalculer les totaux de la commande parente
        if (savedLigne.getCommandeAchatsEntity() != null) {
            recalculerTotauxCommande(savedLigne.getCommandeAchatsEntity().getId());
        }

        return savedLigne;
    }

    @Override
    public void deleteLigneCommandeAchats(int id) {
        LigneCommandeAchatsEntity ligne = ligneCommandeAchatsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne de commande achats non trouvée"));

        Integer commandeId = null;
        if (ligne.getCommandeAchatsEntity() != null) {
            commandeId = ligne.getCommandeAchatsEntity().getId();
        }

        // ✅ Supprimer la ligne
        ligneCommandeAchatsRepository.deleteById(id);

        // ✅ Recalculer les totaux de la commande parente
        if (commandeId != null) {
            recalculerTotauxCommande(commandeId);
        }
    }

    // ✅ Calcul des totaux HT / TTC pour une ligne
    private void calculerTotauxLigne(LigneCommandeAchatsEntity ligne) {
        if (ligne.getProduit() != null && ligne.getQuantite() != null && ligne.getPrixUnitaire() != null) {
            double ht = ligne.getQuantite() * ligne.getPrixUnitaire();
            double ttc = ht * (1 + ligne.getProduit().getTva() / 100);
            ligne.setTotalHT(ht);
            ligne.setTotalTTC(ttc);
        }
    }

    // ✅ Recalculer les totaux de la commande parente
    private void recalculerTotauxCommande(int commandeId) {
        CommandeAchatsEntity commande = commandeAchatsRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande d'achats non trouvée"));

        double totalHT = 0.0;
        double totalTTC = 0.0;

        List<LigneCommandeAchatsEntity> lignes = ligneCommandeAchatsRepository
                .findByCommandeAchatsEntityId(commandeId);

        for (LigneCommandeAchatsEntity ligne : lignes) {
            if (ligne.getTotalHT() != null) {
                totalHT += ligne.getTotalHT();
            }
            if (ligne.getTotalTTC() != null) {
                totalTTC += ligne.getTotalTTC();
            }
        }

        commande.setTotalHT(totalHT);
        commande.setTotalTTC(totalTTC);
        commandeAchatsRepository.save(commande);
    }
}