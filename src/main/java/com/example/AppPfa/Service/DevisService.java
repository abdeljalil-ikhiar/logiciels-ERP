package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.ClientEntity;
import com.example.AppPfa.DAO.Entity.DevisEntity;
import com.example.AppPfa.DAO.Entity.LigneDevisEntity;
import com.example.AppPfa.DAO.Entity.ProduitEntity;
import com.example.AppPfa.Repository.ClientRepository;
import com.example.AppPfa.Repository.DevisRepository;
import com.example.AppPfa.Repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DevisService implements DevisManager {

    @Autowired
    private DevisRepository devisRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Override
    @Transactional
    public DevisEntity addDevis(DevisEntity devisEntity) {
        // Génération numéro unique DV-2025-XX
        int year = LocalDate.now().getYear();
        long countThisYear = devisRepository.countByYear(year); // Méthode à créer dans le repository
        String numero = String.format("DV-%d-%02d", year, countThisYear + 1);
        devisEntity.setNumeroDevis(numero);

        // Charger le client
        if (devisEntity.getClient() != null && devisEntity.getClient().getId() != null) {
            ClientEntity client = clientRepository.findById(devisEntity.getClient().getId())
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            devisEntity.setClient(client);
        }

        // Traiter les lignes
        if (devisEntity.getLignesDevis() != null) {
            for (LigneDevisEntity ligne : devisEntity.getLignesDevis()) {
                if (ligne.getProduit() != null && ligne.getProduit().getId() != null) {
                    ProduitEntity produit = produitRepository.findById(ligne.getProduit().getId())
                            .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
                    ligne.setProduit(produit);
                }
                ligne.setDevis(devisEntity);
            }
        }

        // Calculer les totaux
        calculerTotauxDevis(devisEntity);

        return devisRepository.save(devisEntity);
    }

    @Override
    @Transactional
    public DevisEntity updateDevis(int id, DevisEntity devisEntity) {
        DevisEntity existing = devisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Devis non trouvé"));

        existing.setDateDevis(devisEntity.getDateDevis());

        // Mettre à jour le client
        if (devisEntity.getClient() != null && devisEntity.getClient().getId() != null) {
            ClientEntity client = clientRepository.findById(devisEntity.getClient().getId())
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            existing.setClient(client);
        }

        // Supprimer les anciennes lignes et ajouter les nouvelles
        existing.getLignesDevis().clear();
        if (devisEntity.getLignesDevis() != null) {
            for (LigneDevisEntity ligne : devisEntity.getLignesDevis()) {
                if (ligne.getProduit() != null && ligne.getProduit().getId() != null) {
                    ProduitEntity produit = produitRepository.findById(ligne.getProduit().getId())
                            .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
                    ligne.setProduit(produit);
                }
                ligne.setDevis(existing);
                existing.getLignesDevis().add(ligne);
            }
        }

        calculerTotauxDevis(existing);

        return devisRepository.save(existing);
    }

    @Override
    public List<DevisEntity> getAllDevis() {
        return devisRepository.findAll();
    }

    @Override
    public void deleteDevis(int id) {
        devisRepository.deleteById(id);
    }

    @Override
    public void calculerTotauxDevis(DevisEntity devisEntity) {
        double totalHT = 0;
        double totalTTC = 0;

        if (devisEntity.getLignesDevis() != null) {
            for (LigneDevisEntity ligne : devisEntity.getLignesDevis()) {
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

        devisEntity.setTotalHT(totalHT);
        devisEntity.setTotalTTC(totalTTC);
    }
}
