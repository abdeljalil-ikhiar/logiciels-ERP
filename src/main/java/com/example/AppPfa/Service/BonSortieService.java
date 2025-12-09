package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.*;
import com.example.AppPfa.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BonSortieService implements BonSortieManager {

    @Autowired
    private BonSortieRepository bonSortieRepository;
    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;
    @Autowired
    private ProduitRepository produitRepository;
    @Autowired
    private BonLivraisonService bonLivraisonService; // NOUVEAU

    /** AJOUT BON DE SORTIE **/
    @Override
    @Transactional
    public BonSortieEntity addBonSortie(BonSortieEntity bonSortieEntity) {
        if (bonSortieEntity.getCommandeEntity() == null || bonSortieEntity.getCommandeEntity().getId() == null) {
            throw new IllegalArgumentException("La commande est obligatoire");
        }
        if (bonSortieEntity.getLigneBonSortieEntities() == null || bonSortieEntity.getLigneBonSortieEntities().isEmpty()) {
            throw new IllegalArgumentException("Au moins une ligne est obligatoire");
        }

        // Charger la commande complète
        CommandeEntity commande = commandeRepository.findById(bonSortieEntity.getCommandeEntity().getId())
                .orElseThrow(() -> new NoSuchElementException("Commande introuvable"));

        bonSortieEntity.setCommandeEntity(commande);
        bonSortieEntity.setNumeroBonSortie(generateNumeroBonSortie());
        if (bonSortieEntity.getDateSortie() == null) {
            bonSortieEntity.setDateSortie(LocalDate.now());
        }

        // Traiter les lignes
        bonSortieEntity.getLigneBonSortieEntities().forEach(ligne -> {
            LigneCommandeEntity ligneCommande = ligneCommandeRepository.findById(ligne.getLigneCommande().getId())
                    .orElseThrow(() -> new NoSuchElementException("Ligne de commande introuvable"));
            ligne.setLigneCommande(ligneCommande);

            if (ligne.getProduitEchange() != null && ligne.getProduitEchange().getId() != null) {
                ProduitEntity produitEchange = produitRepository.findById(ligne.getProduitEchange().getId())
                        .orElseThrow(() -> new NoSuchElementException("Produit échangé introuvable"));
                ligne.setProduitEchange(produitEchange);
            }

            ligne.setBonSortie(bonSortieEntity);
        });

        return bonSortieRepository.save(bonSortieEntity);
    }

    /** MISE À JOUR **/
    @Override
    @Transactional
    public BonSortieEntity updateBonSortie(int id, BonSortieEntity bonSortieEntity) {
        BonSortieEntity existing = bonSortieRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bon de sortie introuvable"));

        if (bonSortieEntity.getDateSortie() != null) {
            existing.setDateSortie(bonSortieEntity.getDateSortie());
        }

        if (bonSortieEntity.getCommandeEntity() != null && bonSortieEntity.getCommandeEntity().getId() != null) {
            CommandeEntity commande = commandeRepository.findById(bonSortieEntity.getCommandeEntity().getId())
                    .orElseThrow(() -> new NoSuchElementException("Commande introuvable"));
            existing.setCommandeEntity(commande);
        }

        existing.getLigneBonSortieEntities().clear();
        if (bonSortieEntity.getLigneBonSortieEntities() != null) {
            bonSortieEntity.getLigneBonSortieEntities().forEach(ligne -> {
                LigneCommandeEntity ligneCommande = ligneCommandeRepository.findById(ligne.getLigneCommande().getId())
                        .orElseThrow(() -> new NoSuchElementException("Ligne de commande introuvable"));
                ligne.setLigneCommande(ligneCommande);

                if (ligne.getProduitEchange() != null && ligne.getProduitEchange().getId() != null) {
                    ProduitEntity produitEchange = produitRepository.findById(ligne.getProduitEchange().getId())
                            .orElseThrow(() -> new NoSuchElementException("Produit échangé introuvable"));
                    ligne.setProduitEchange(produitEchange);
                }

                ligne.setBonSortie(existing);
                existing.getLigneBonSortieEntities().add(ligne);
            });
        }

        // Sauvegarder les modifications
        BonSortieEntity savedBonSortie = bonSortieRepository.save(existing);

        // 🔄 NOUVEAU : Recalculer le bon de livraison s'il existe
        bonLivraisonService.recalculerBonLivraison(id);

        return savedBonSortie;
    }

    /** LISTER TOUS **/
    @Override
    public List<BonSortieEntity> getBonSortie() {
        return bonSortieRepository.findAll();
    }

    /** SUPPRIMER **/
    @Override
    @Transactional
    public void deleteBonSortie(int id) {
        if (!bonSortieRepository.existsById(id)) {
            throw new NoSuchElementException("Bon de sortie introuvable");
        }
        bonSortieRepository.deleteById(id);
    }

    /** GÉNÉRER NUMÉRO **/
    private String generateNumeroBonSortie() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long countToday = bonSortieRepository.countByNumeroBonSortieStartingWith("BS-" + datePart + "-");
        return String.format("BS-%s-%03d", datePart, countToday + 1);
    }
}