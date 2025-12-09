package com.example.AppPfa.Service;
import com.example.AppPfa.DAO.Entity.*;
import com.example.AppPfa.Repository.BonLivraisonRepository;
import com.example.AppPfa.Repository.LigneBonLivraisonRepository;

import com.example.AppPfa.Repository.LigneBonSortieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

public class LigneBonLivraisonService implements LigneBonLivraisonManager {

   @Autowired
   private LigneBonLivraisonRepository ligneBonLivraisonRepository;
    @Autowired
    private BonLivraisonRepository bonLivraisonRepository;
    @Autowired
    private LigneBonSortieRepository ligneBonSortieRepository;

    @Override
    @Transactional(readOnly = true)
    public LigneBonLivraisonEntity getLigneBonLivraisonById(Integer id) {
        return ligneBonLivraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne de bon de livraison non trouvée avec l'ID: " + id));
    }

    /**
     * Récupérer toutes les lignes de bon de livraison
     */
    @Override
    @Transactional(readOnly = true)
    public List<LigneBonLivraisonEntity> getLigneBonLivraison() {
        return ligneBonLivraisonRepository.findAll();
    }

    /**
     * Récupérer les lignes d'un bon de livraison spécifique
     */
    @Override
    @Transactional(readOnly = true)
    public List<LigneBonLivraisonEntity> getLigneBonLivraisonByBonLivraison(Integer bonLivraisonId) {
        return ligneBonLivraisonRepository.findByBonLivraisonId(bonLivraisonId);
    }

    /**
     * Supprimer une ligne de bon de livraison
     * ⚠️ À utiliser uniquement pour corriger des erreurs
     */
    @Override
    @Transactional
    public void deleteLigneBonLivraison(Integer id) {
        // Vérifier que la ligne existe et récupérer l'ID du bon de livraison parent
        LigneBonLivraisonEntity ligne = ligneBonLivraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne de bon de livraison non trouvée avec l'ID: " + id));

        Integer bonLivraisonId = ligne.getBonLivraison().getId();

        // Supprimer la ligne
        ligneBonLivraisonRepository.deleteById(id);

        // Recalculer les totaux du bon de livraison parent
        recalculerTotauxBonLivraison(bonLivraisonId);
    }

    /**
     * Recalculer et mettre à jour les totaux d'un bon de livraison
     * Méthode privée utilisée après suppression d'une ligne
     */
    private void recalculerTotauxBonLivraison(Integer bonLivraisonId) {
        // Récupérer le bon de livraison
        BonLivraisonEntity bonLivraison = bonLivraisonRepository.findById(bonLivraisonId)
                .orElseThrow(() -> new RuntimeException("Bon de livraison non trouvé avec l'ID: " + bonLivraisonId));

        // Récupérer toutes les lignes du bon de livraison
        List<LigneBonLivraisonEntity> lignes = ligneBonLivraisonRepository.findByBonLivraisonId(bonLivraisonId);

        // Calculer les totaux
        double totalHT = 0.0;
        double totalTVA = 0.0;

        for (LigneBonLivraisonEntity ligne : lignes) {
            totalHT += ligne.getTotalHT();
            totalTVA += ligne.getTotalTVA();
        }

        // Mettre à jour le bon de livraison
        bonLivraison.setTotalHT(totalHT);
        bonLivraison.setTotalTVA(totalTVA);
        bonLivraison.setTotalTTC(totalHT + totalTVA);

        bonLivraisonRepository.save(bonLivraison);
    }
    @Override
    @Transactional
    public LigneBonLivraisonEntity addLigneBonLivraison(Integer bonLivraisonId, Integer ligneBonSortieId) {
        // 1. Récupérer les entités nécessaires depuis la base de données
        BonLivraisonEntity bonLivraison = bonLivraisonRepository.findById(bonLivraisonId)
                .orElseThrow(() -> new RuntimeException("Bon de livraison non trouvé avec l'ID: " + bonLivraisonId));

        LigneBonSortieEntity ligneBonSortie = ligneBonSortieRepository.findById(ligneBonSortieId)
                .orElseThrow(() -> new RuntimeException("Ligne de bon de sortie non trouvée avec l'ID: " + ligneBonSortieId));

        // Il est bon de vérifier si cette ligne de sortie n'est pas déjà dans un autre bon de livraison
        // (Logique à ajouter si nécessaire)

        // 2. Récupérer les informations pour le calcul depuis les entités liées
        LigneCommandeEntity ligneCommande = ligneBonSortie.getLigneCommande();
        if (ligneCommande == null) {
            throw new IllegalStateException("La ligne de bon de sortie " + ligneBonSortieId + " n'est pas liée à une ligne de commande.");
        }

        // Supposons que ces getters existent dans vos entités
        Double quantiteLivree = ligneBonSortie.getQuantiteSortie();
        Double prixUnitaire = ligneCommande.getPrixUnitaire();
        Double tauxTVA = ligneCommande.getProduit().getTva().doubleValue();

        // 3. Calculer les totaux pour la nouvelle ligne
        double totalHT = quantiteLivree * prixUnitaire;
        double totalTVA = totalHT * (tauxTVA / 100.0);
        double totalTTC = totalHT + totalTVA;

        // 4. Construire la nouvelle LigneBonLivraisonEntity
        LigneBonLivraisonEntity nouvelleLigne = LigneBonLivraisonEntity.builder()
                .bonLivraison(bonLivraison)
                .ligneBonSortie(ligneBonSortie)
                .ligneCommande(ligneCommande) // Lier aussi la ligne de commande pour un accès direct
                .totalHT(totalHT)
                .totalTVA(totalTVA)
                .totalTTC(totalTTC)
                .build();

        // 5. Enregistrer la nouvelle ligne
        LigneBonLivraisonEntity ligneEnregistree = ligneBonLivraisonRepository.save(nouvelleLigne);

        // 6. Mettre à jour les totaux du bon de livraison parent
        recalculerTotauxBonLivraison(bonLivraisonId);

        // 7. Retourner la ligne enregistrée
        return ligneEnregistree;
    }
}