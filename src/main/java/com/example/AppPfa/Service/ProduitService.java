package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.ProduitEntity;
import com.example.AppPfa.Repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProduitService implements ProduitManager {

    @Autowired
    private ProduitRepository produitRepository;

    @Override
    public ProduitEntity addProduit(ProduitEntity produitEntity) {
        return produitRepository.save(produitEntity);
    }

    @Override
    public ProduitEntity updateProduit(int id, ProduitEntity produitEntity) {
        ProduitEntity existing = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit not found with id: " + id));

        existing.setReferences(produitEntity.getReferences());
        existing.setDesignation(produitEntity.getDesignation());
        existing.setTva(produitEntity.getTva());
        existing.setCategorie(produitEntity.getCategorie());

        return produitRepository.save(existing);
    }

    @Override
    public void deleteProduit(int id) {
        if (!produitRepository.existsById(id)) {
            throw new RuntimeException("Produit not found with id: " + id);
        }
        produitRepository.deleteById(id);
    }

    @Override
    public ProduitEntity getProduitById(int id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit not found with id: " + id));
    }

    @Override
    public List<ProduitEntity> getAllProduit() {
        return produitRepository.findAll();
    }

    @Override
    public List<ProduitEntity> getProduitsByCategorieId(int categorieId) {
        return produitRepository.findByCategorie_Id(categorieId);
    }
}