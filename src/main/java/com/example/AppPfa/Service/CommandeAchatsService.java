package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.*;
import com.example.AppPfa.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class CommandeAchatsService implements CommandeAchatsManager {

    @Autowired
    private CommandeAchatsRepository commandeAchatsRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @Override
    public CommandeAchatsEntity addCommandeAchats(CommandeAchatsEntity commandeAchatsEntity) {
        System.out.println("📝 Création de la commande d'achats...");

        // ✅ Génération numéro unique (ex: BCA-2025-01)
        if (commandeAchatsEntity.getNumerocommandeAchats() == null ||
                commandeAchatsEntity.getNumerocommandeAchats().isEmpty()) {
            int year = LocalDate.now().getYear();
            long count = commandeAchatsRepository.count();
            String numero = String.format("BCA-%d-%03d", year, count + 1);
            commandeAchatsEntity.setNumerocommandeAchats(numero);
        }

        // ✅ Date par défaut si non fournie
        if (commandeAchatsEntity.getDatecommandeAchats() == null) {
            commandeAchatsEntity.setDatecommandeAchats(LocalDate.now());
        }

        // ✅ Charger le fournisseur depuis la BDD
        if (commandeAchatsEntity.getFournisseurEntity() != null &&
                commandeAchatsEntity.getFournisseurEntity().getId() != null) {
            System.out.println("📦 Chargement du fournisseur ID: " +
                    commandeAchatsEntity.getFournisseurEntity().getId());

            FournisseurEntity fournisseur = fournisseurRepository
                    .findById(commandeAchatsEntity.getFournisseurEntity().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Fournisseur non trouvé avec l'ID: " +
                                    commandeAchatsEntity.getFournisseurEntity().getId()));
            commandeAchatsEntity.setFournisseurEntity(fournisseur);
        }

        // ✅ Traiter les lignes de commande achats
        if (commandeAchatsEntity.getListAchats() != null &&
                !commandeAchatsEntity.getListAchats().isEmpty()) {

            System.out.println("📋 Traitement de " +
                    commandeAchatsEntity.getListAchats().size() + " lignes");

            for (LigneCommandeAchatsEntity ligne : commandeAchatsEntity.getListAchats()) {
                // Charger le produit
                if (ligne.getProduit() != null && ligne.getProduit().getId() != null) {
                    ProduitEntity produit = produitRepository
                            .findById(ligne.getProduit().getId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Produit non trouvé avec l'ID: " + ligne.getProduit().getId()));
                    ligne.setProduit(produit);
                }

                // ✅ CRUCIAL: Lier la ligne à la commande (relation bidirectionnelle)
                ligne.setCommandeAchatsEntity(commandeAchatsEntity);

                // Calculer les totaux de la ligne
                calculerTotauxLigne(ligne);
            }
        }

        // ✅ Calcul des totaux globaux HT / TTC
        calculerTotauxCommande(commandeAchatsEntity);

        // ✅ Sauvegarde finale (cascade sur les lignes)
        CommandeAchatsEntity saved = commandeAchatsRepository.save(commandeAchatsEntity);

        System.out.println("✅ Commande sauvegardée avec ID: " + saved.getId());
        System.out.println("💰 Total HT: " + saved.getTotalHT() + " | Total TTC: " + saved.getTotalTTC());

        return saved;
    }

    @Override
    public List<CommandeAchatsEntity> getCommandeAchats() {
        return commandeAchatsRepository.findAll();
    }

    @Override
    public CommandeAchatsEntity updateCommandeAchats(int id, CommandeAchatsEntity commandeAchatsEntity) {
        System.out.println("🔄 Mise à jour de la commande ID: " + id);

        CommandeAchatsEntity existing = commandeAchatsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande d'achats non trouvée avec l'ID: " + id));

        // Mise à jour des champs simples
        existing.setDatecommandeAchats(commandeAchatsEntity.getDatecommandeAchats());
        existing.setNumerocommandeAchats(commandeAchatsEntity.getNumerocommandeAchats());

        // Mise à jour du fournisseur
        if (commandeAchatsEntity.getFournisseurEntity() != null &&
                commandeAchatsEntity.getFournisseurEntity().getId() != null) {
            FournisseurEntity fournisseur = fournisseurRepository
                    .findById(commandeAchatsEntity.getFournisseurEntity().getId())
                    .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé"));
            existing.setFournisseurEntity(fournisseur);
        }

        // ✅ Supprimer anciennes lignes (orphanRemoval = true le fera automatiquement)
        existing.getListAchats().clear();

        // Ajouter nouvelles lignes
        if (commandeAchatsEntity.getListAchats() != null) {
            for (LigneCommandeAchatsEntity ligne : commandeAchatsEntity.getListAchats()) {
                if (ligne.getProduit() != null && ligne.getProduit().getId() != null) {
                    ProduitEntity produit = produitRepository
                            .findById(ligne.getProduit().getId())
                            .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
                    ligne.setProduit(produit);
                }

                // ✅ Lier la ligne à la commande
                ligne.setCommandeAchatsEntity(existing);
                calculerTotauxLigne(ligne);
                existing.getListAchats().add(ligne);
            }
        }

        calculerTotauxCommande(existing);
        return commandeAchatsRepository.save(existing);
    }

    @Override
    public void deleteCommandeAchats(int id) {
        if (!commandeAchatsRepository.existsById(id)) {
            throw new RuntimeException("Commande d'achats non trouvée avec l'ID: " + id);
        }
        commandeAchatsRepository.deleteById(id);
        System.out.println("🗑️ Commande ID " + id + " supprimée");
    }

    // ✅ Calcul des totaux d'une ligne
    private void calculerTotauxLigne(LigneCommandeAchatsEntity ligne) {
        if (ligne.getProduit() != null && ligne.getQuantite() != null && ligne.getPrixUnitaire() != null) {
            double ht = ligne.getQuantite() * ligne.getPrixUnitaire();
            double tva = ligne.getProduit().getTva() != null ? ligne.getProduit().getTva() : 20.0;
            double ttc = ht * (1 + tva / 100);

            ligne.setTotalHT(ht);
            ligne.setTotalTTC(ttc);
        }
    }

    // ✅ Calcul des totaux globaux de la commande
    private void calculerTotauxCommande(CommandeAchatsEntity commandeAchatsEntity) {
        double totalHT = 0.0;
        double totalTTC = 0.0;

        if (commandeAchatsEntity.getListAchats() != null) {
            for (LigneCommandeAchatsEntity ligne : commandeAchatsEntity.getListAchats()) {
                if (ligne.getTotalHT() != null) {
                    totalHT += ligne.getTotalHT();
                }
                if (ligne.getTotalTTC() != null) {
                    totalTTC += ligne.getTotalTTC();
                }
            }
        }

        commandeAchatsEntity.setTotalHT(totalHT);
        commandeAchatsEntity.setTotalTTC(totalTTC);
    }
}