package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.*;
import com.example.AppPfa.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommandeService implements CommandeManager {

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LigneBonSortieRepository ligneBonSortieRepository;

    @Autowired
    private BonSortieRepository bonSortieRepository;

    @Override
    public CommandeEntity addCommande(CommandeEntity commandeEntity) {
        // ✅ Génération numéro unique (ex: BC-2025-01)
        int year = LocalDate.now().getYear();
        long count = commandeRepository.count();
        String numero = String.format("BC-%d-%02d", year, count + 1);
        commandeEntity.setNumerocommande(numero);

        // ✅ Charger le client depuis la BDD
        if (commandeEntity.getClient() != null && commandeEntity.getClient().getId() != null) {
            ClientEntity client = clientRepository.findById(commandeEntity.getClient().getId())
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            commandeEntity.setClient(client);
        }

        // ✅ Traiter les lignes de commande
        if (commandeEntity.getLignesCommande() != null) {
            for (LigneCommandeEntity ligne : commandeEntity.getLignesCommande()) {

                // Charger le produit
                if (ligne.getProduit() != null && ligne.getProduit().getId() != null) {
                    ProduitEntity produit = produitRepository.findById(ligne.getProduit().getId())
                            .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
                    ligne.setProduit(produit);
                }

                // Lier la ligne à la commande
                ligne.setCommandeEntity(commandeEntity);
            }
        }

        // ✅ Calcul des totaux HT / TTC
        calculerTotauxCommande(commandeEntity);

        // ✅ Sauvegarde finale
        return commandeRepository.save(commandeEntity);
    }

    @Override
    public List<CommandeEntity> getCommande() {
        return commandeRepository.findAll();
    }

    @Override
    public CommandeEntity updateCommande(int id, CommandeEntity commandeEntity) {
        CommandeEntity existing = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        existing.setDatecommande(commandeEntity.getDatecommande());

        // Mise à jour du client
        if (commandeEntity.getClient() != null && commandeEntity.getClient().getId() != null) {
            ClientEntity client = clientRepository.findById(commandeEntity.getClient().getId())
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            existing.setClient(client);
        }

        // 🔥 ÉTAPE 1 : Récupérer les IDs des bons de sortie concernés AVANT suppression
        List<Integer> bonSortieIds = existing.getLignesCommande().stream()
                .flatMap(ligne -> ligne.getLignesBonSortie().stream())
                .map(lbs -> lbs.getBonSortie().getId())
                .distinct()
                .collect(Collectors.toList());

        // 🔥 ÉTAPE 2 : Supprimer les lignes de bon de sortie associées
        existing.getLignesCommande().forEach(ligne -> {
            if (!ligne.getLignesBonSortie().isEmpty()) {
                ligneBonSortieRepository.deleteAll(ligne.getLignesBonSortie());
                ligne.getLignesBonSortie().clear();
            }
        });

        // Supprimer anciennes lignes de commande
        existing.getLignesCommande().clear();

        // Ajouter nouvelles lignes
        if (commandeEntity.getLignesCommande() != null) {
            for (LigneCommandeEntity ligne : commandeEntity.getLignesCommande()) {
                if (ligne.getProduit() != null && ligne.getProduit().getId() != null) {
                    ProduitEntity produit = produitRepository.findById(ligne.getProduit().getId())
                            .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
                    ligne.setProduit(produit);
                }
                ligne.setCommandeEntity(existing);
                existing.getLignesCommande().add(ligne);
            }
        }

        calculerTotauxCommande(existing);
        CommandeEntity saved = commandeRepository.save(existing);

        // 🧹 ÉTAPE 3 : Nettoyer les bons de sortie vides
        nettoyerBonsSortieVides(bonSortieIds);

        return saved;
    }

    @Override
    public void deleteCommande(int id) {
        // 🔥 Récupérer les bons de sortie avant suppression
        CommandeEntity commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        List<Integer> bonSortieIds = commande.getLignesCommande().stream()
                .flatMap(ligne -> ligne.getLignesBonSortie().stream())
                .map(lbs -> lbs.getBonSortie().getId())
                .distinct()
                .collect(Collectors.toList());

        // Supprimer la commande (cascade supprimera les lignes)
        commandeRepository.deleteById(id);

        // 🧹 Nettoyer les bons de sortie vides
        nettoyerBonsSortieVides(bonSortieIds);
    }

    // 🆕 Méthode pour nettoyer les bons de sortie vides
    private void nettoyerBonsSortieVides(List<Integer> bonSortieIds) {
        bonSortieIds.forEach(bonSortieId -> {
            bonSortieRepository.findById(bonSortieId).ifPresent(bonSortie -> {
                // Forcer le rechargement des lignes
                if (bonSortie.getLigneBonSortieEntities() == null ||
                        bonSortie.getLigneBonSortieEntities().isEmpty()) {
                    bonSortieRepository.deleteById(bonSortieId);
                }
            });
        });
    }

    // ✅ Calcul des totaux HT / TTC
    private void calculerTotauxCommande(CommandeEntity commandeEntity) {
        double totalHT = 0.0;
        double totalTTC = 0.0;

        if (commandeEntity.getLignesCommande() != null) {
            for (LigneCommandeEntity ligne : commandeEntity.getLignesCommande()) {
                if (ligne.getProduit() != null) {
                    double ht = ligne.getQuantite() * ligne.getPrixUnitaire();
                    double ttc = ht * (1 + ligne.getProduit().getTva() / 100);
                    ligne.setTotalHT(ht);
                    ligne.setTotalTTC(ttc);
                    totalHT += ht;
                    totalTTC += ttc;
                }
            }
        }

        commandeEntity.setTotalHT(totalHT);
        commandeEntity.setTotalTTC(totalTTC);
    }
}