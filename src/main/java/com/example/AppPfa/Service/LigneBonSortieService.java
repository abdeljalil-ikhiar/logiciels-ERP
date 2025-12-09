package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.LigneBonSortieEntity;
import com.example.AppPfa.Repository.LigneBonSortieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class LigneBonSortieService implements LigneBonSortieManager {

    @Autowired
    private LigneBonSortieRepository ligneBonSortieRepository;
    @Autowired
    private BonLivraisonService bonLivraisonService; // NOUVEAU

    @Override
    @Transactional
    public LigneBonSortieEntity addLigneBonSortie(LigneBonSortieEntity ligneBonSortieEntity) {
        // Vérifie si une ligne existe déjà pour le même produit et le même bon de sortie
        Optional<LigneBonSortieEntity> existingLigne = ligneBonSortieRepository
                .findByProduitEchangeIdAndBonSortieId(
                        ligneBonSortieEntity.getProduitEchange().getId(),
                        ligneBonSortieEntity.getBonSortie().getId()
                );

        if (existingLigne.isPresent()) {
            // Si elle existe déjà, on empêche l'ajout
            throw new IllegalStateException("Une ligne pour ce produit existe déjà dans ce bon de sortie.");
        }

        return ligneBonSortieRepository.save(ligneBonSortieEntity);
    }

    @Override
    @Transactional
    public LigneBonSortieEntity updateLigneBonSortie(int id, LigneBonSortieEntity ligneBonSortieEntity) {
        LigneBonSortieEntity existing = ligneBonSortieRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("LigneBonSortie avec ID " + id + " introuvable"));

        // Garder l'ID du bon de sortie pour le recalcul
        Integer bonSortieId = existing.getBonSortie().getId();

        existing.setQuantiteSortie(ligneBonSortieEntity.getQuantiteSortie());
        existing.setRaisonEchange(ligneBonSortieEntity.getRaisonEchange());
        existing.setProduitEchange(ligneBonSortieEntity.getProduitEchange());

        LigneBonSortieEntity saved = ligneBonSortieRepository.save(existing);

        // 🔄 NOUVEAU : Recalculer le bon de livraison s'il existe
        bonLivraisonService.recalculerBonLivraison(bonSortieId);

        return saved;
    }

    @Override
    public List<LigneBonSortieEntity> getAllLigneBonSortie() {
        return ligneBonSortieRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteLigneBonSortie(int id) {
        if (!ligneBonSortieRepository.existsById(id)) {
            throw new NoSuchElementException("LigneBonSortie avec ID " + id + " introuvable");
        }
        ligneBonSortieRepository.deleteById(id);
    }
}