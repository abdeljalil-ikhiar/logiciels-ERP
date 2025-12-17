package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.InventaireEntity;
import com.example.AppPfa.DAO.Entity.LigneInventaireEntity;
import com.example.AppPfa.DAO.Entity.ProduitEntity;
import com.example.AppPfa.Repository.InventaireRepository;
import com.example.AppPfa.Repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class InventaireService implements InventaireManager {

    @Autowired
    private InventaireRepository inventaireRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Override
    public InventaireEntity addInventaire(InventaireEntity inventaire) {
        String datePart = LocalDate.now().toString().replace("-", "");
        long count = inventaireRepository.count() + 1;
        inventaire.setNumeroInventaire("INV-" + datePart + "-" + String.format("%04d", count));

        if (inventaire.getDateInventaire() == null) {
            inventaire.setDateInventaire(LocalDate.now());
        }

        if (inventaire.getLigneInventaireEntities() != null) {
            for (LigneInventaireEntity ligne : inventaire.getLigneInventaireEntities()) {
                ligne.setInventaire(inventaire);

                if (ligne.getProduit() != null && ligne.getProduit().getId() != null) {
                    ProduitEntity produit = produitRepository.findById(ligne.getProduit().getId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Produit not found with id: " + ligne.getProduit().getId()));
                    ligne.setProduit(produit);
                }
            }
        }

        return inventaireRepository.save(inventaire);
    }

    @Override
    public InventaireEntity updateInventaire(Integer id, InventaireEntity inventaire) {
        InventaireEntity existing = inventaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventaire not found with id: " + id));

        existing.setDateInventaire(inventaire.getDateInventaire());

        if (inventaire.getLigneInventaireEntities() != null) {
            existing.getLigneInventaireEntities().clear();

            for (LigneInventaireEntity ligne : inventaire.getLigneInventaireEntities()) {
                LigneInventaireEntity nouvelleLigne = new LigneInventaireEntity();
                nouvelleLigne.setQteinventaire(ligne.getQteinventaire());
                nouvelleLigne.setNamezone(ligne.getNamezone());
                nouvelleLigne.setObservations(ligne.getObservations());
                nouvelleLigne.setInventaire(existing);

                if (ligne.getProduit() != null && ligne.getProduit().getId() != null) {
                    ProduitEntity produit = produitRepository.findById(ligne.getProduit().getId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Produit not found with id: " + ligne.getProduit().getId()));
                    nouvelleLigne.setProduit(produit);
                }

                existing.getLigneInventaireEntities().add(nouvelleLigne);
            }
        }

        return inventaireRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventaireEntity> getAllInventaires() {
        return inventaireRepository.findAll();
    }

    @Override
    public void deleteInventaire(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (!inventaireRepository.existsById(id)) {
            throw new RuntimeException("Inventaire not found with id: " + id);
        }
        inventaireRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public InventaireEntity getInventaireById(Integer id) {
        return inventaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventaire not found with id: " + id));
    }
}