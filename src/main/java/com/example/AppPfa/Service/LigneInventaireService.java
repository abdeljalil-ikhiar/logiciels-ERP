package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.LigneInventaireEntity;
import com.example.AppPfa.DAO.Entity.ProduitEntity;
import com.example.AppPfa.DAO.Entity.InventaireEntity;
import com.example.AppPfa.Repository.LigneInventaireRepository;
import com.example.AppPfa.Repository.ProduitRepository;
import com.example.AppPfa.Repository.InventaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LigneInventaireService implements LigneInventaireManager {

    @Autowired
    private LigneInventaireRepository ligneInventaireRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private InventaireRepository inventaireRepository;

    @Override
    public LigneInventaireEntity addLigneInventaire(LigneInventaireEntity ligneInventaireEntity) {
        if (ligneInventaireEntity.getProduit() != null && ligneInventaireEntity.getProduit().getId() != null) {
            ProduitEntity produit = produitRepository.findById(ligneInventaireEntity.getProduit().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Produit not found with id: " + ligneInventaireEntity.getProduit().getId()));
            ligneInventaireEntity.setProduit(produit);
        }

        if (ligneInventaireEntity.getInventaire() != null && ligneInventaireEntity.getInventaire().getId() != null) {
            InventaireEntity inventaire = inventaireRepository.findById(ligneInventaireEntity.getInventaire().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Inventaire not found with id: " + ligneInventaireEntity.getInventaire().getId()));
            ligneInventaireEntity.setInventaire(inventaire);
        }

        return ligneInventaireRepository.save(ligneInventaireEntity);
    }

    @Override
    public LigneInventaireEntity updateLigneInventaire(int id, LigneInventaireEntity ligneInventaireEntity) {
        LigneInventaireEntity existing = ligneInventaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LigneInventaire not found with id: " + id));

        if (ligneInventaireEntity.getProduit() != null && ligneInventaireEntity.getProduit().getId() != null) {
            ProduitEntity produit = produitRepository.findById(ligneInventaireEntity.getProduit().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Produit not found with id: " + ligneInventaireEntity.getProduit().getId()));
            existing.setProduit(produit);
        }

        if (ligneInventaireEntity.getInventaire() != null && ligneInventaireEntity.getInventaire().getId() != null) {
            InventaireEntity inventaire = inventaireRepository.findById(ligneInventaireEntity.getInventaire().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Inventaire not found with id: " + ligneInventaireEntity.getInventaire().getId()));
            existing.setInventaire(inventaire);
        }

        existing.setQteinventaire(ligneInventaireEntity.getQteinventaire());
        existing.setNamezone(ligneInventaireEntity.getNamezone());
        existing.setObservations(ligneInventaireEntity.getObservations());

        return ligneInventaireRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LigneInventaireEntity> getAllLigneInventaire() {
        return ligneInventaireRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<LigneInventaireEntity> getLignesByInventaireId(Integer inventaireId) {
        return ligneInventaireRepository.findByInventaireId(inventaireId);
    }

    @Override
    public void deleteLigneInventaire(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (!ligneInventaireRepository.existsById(id)) {
            throw new RuntimeException("LigneInventaire not found with id: " + id);
        }
        ligneInventaireRepository.deleteById(id);
    }
}