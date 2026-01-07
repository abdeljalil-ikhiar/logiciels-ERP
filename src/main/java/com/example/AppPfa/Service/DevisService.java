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
        long countThisYear = devisRepository.countByYear(year);
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

                // ✅ التأكد من القيم الافتراضية
                if (ligne.getRemisePourcentage() == null) {
                    ligne.setRemisePourcentage(0.0);
                }
                if (ligne.getQuantite() == null) {
                    ligne.setQuantite(0.0);
                }
                if (ligne.getPrixUnitaire() == null) {
                    ligne.setPrixUnitaire(0.0);
                }
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

                // ✅ التأكد من القيم الافتراضية
                if (ligne.getRemisePourcentage() == null) {
                    ligne.setRemisePourcentage(0.0);
                }
                if (ligne.getQuantite() == null) {
                    ligne.setQuantite(0.0);
                }
                if (ligne.getPrixUnitaire() == null) {
                    ligne.setPrixUnitaire(0.0);
                }

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
        double totalRemise = 0;

        if (devisEntity.getLignesDevis() != null) {
            for (LigneDevisEntity ligne : devisEntity.getLignesDevis()) {
                if (ligne.getProduit() != null) {
                    double quantite = ligne.getQuantite() != null ? ligne.getQuantite() : 0.0;
                    double prixUnitaire = ligne.getPrixUnitaire() != null ? ligne.getPrixUnitaire() : 0.0;

                    // Calcul HT avant remise
                    double htBrut = quantite * prixUnitaire;

                    // Appliquer remise %
                    Double remise = ligne.getRemisePourcentage() != null ? ligne.getRemisePourcentage() : 0.0;
                    if (remise < 0) remise = 0.0;
                    if (remise > 100) remise = 100.0;

                    double montantRemise = htBrut * remise / 100;
                    double ht = htBrut - montantRemise;

                    // Calcul TTC avec TVA du produit
                    double tva = ligne.getProduit().getTva() != null ? ligne.getProduit().getTva() : 0.0;
                    double ttc = ht * (1 + tva / 100);

                    // Mettre à jour la ligne
                    ligne.setTotalHT(Math.round(ht * 100.0) / 100.0);
                    ligne.setTotalTTC(Math.round(ttc * 100.0) / 100.0);

                    // Ajouter au total du devis
                    totalHT += ht;
                    totalTTC += ttc;
                    totalRemise += montantRemise;
                }
            }
        }

        devisEntity.setTotalHT(Math.round(totalHT * 100.0) / 100.0);
        devisEntity.setTotalTTC(Math.round(totalTTC * 100.0) / 100.0);
        devisEntity.setTotalRemise(Math.round(totalRemise * 100.0) / 100.0);
    }
}