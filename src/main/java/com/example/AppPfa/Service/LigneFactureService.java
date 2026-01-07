package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.*;
import com.example.AppPfa.Repository.FactureRepository;
import com.example.AppPfa.Repository.LigneBonLivraisonRepository;
import com.example.AppPfa.Repository.LigneFactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LigneFactureService implements LigneFactureManager {

    @Autowired
    private LigneFactureRepository ligneFactureRepository;

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private LigneBonLivraisonRepository ligneBonLivraisonRepository;

    @Override
    @Transactional(readOnly = true)
    public LigneFactureEntity getLigneFactureById(Integer id) {
        return ligneFactureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne de facture introuvable avec ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LigneFactureEntity> getAllLignesFacture() {
        return ligneFactureRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LigneFactureEntity> getLignesByFactureId(Integer factureId) {
        return ligneFactureRepository.findByFactureId(factureId);
    }

    /**
     * ✅ Ajouter une ligne de facture avec remise
     */
    @Override
    @Transactional
    public LigneFactureEntity addLigneFacture(Integer factureId, Integer ligneBonLivraisonId) {

        FactureEntity facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture introuvable avec ID : " + factureId));

        LigneBonLivraisonEntity ligneBonLivraison = ligneBonLivraisonRepository.findById(ligneBonLivraisonId)
                .orElseThrow(() -> new RuntimeException("Ligne de bon de livraison introuvable avec ID : " + ligneBonLivraisonId));

        LigneCommandeEntity ligneCommande = ligneBonLivraison.getLigneCommande();
        if (ligneCommande == null) {
            throw new IllegalStateException("La ligne de bon de livraison n'est pas liée à une ligne de commande.");
        }

        // ✅ Récupérer les valeurs avec remise depuis LigneBonLivraison
        Double totalHT = ligneBonLivraison.getTotalHT() != null ? ligneBonLivraison.getTotalHT() : 0.0;
        Double totalTVA = ligneBonLivraison.getTotalTVA() != null ? ligneBonLivraison.getTotalTVA() : 0.0;
        Double totalTTC = ligneBonLivraison.getTotalTTC() != null ? ligneBonLivraison.getTotalTTC() : 0.0;
        Double remiseAppliquee = ligneBonLivraison.getRemiseAppliquee() != null ? ligneBonLivraison.getRemiseAppliquee() : 0.0;
        Double montantRemise = ligneBonLivraison.getMontantRemise() != null ? ligneBonLivraison.getMontantRemise() : 0.0;

        // ✅ Construction de la ligne de facture avec remise
        LigneFactureEntity nouvelleLigne = LigneFactureEntity.builder()
                .facture(facture)
                .ligneBonLivraison(ligneBonLivraison)
                .ligneCommande(ligneCommande)
                .totalHT(arrondir(totalHT))
                .totalTVA(arrondir(totalTVA))
                .totalTTC(arrondir(totalTTC))
                .remiseAppliquee(remiseAppliquee)
                .montantRemise(arrondir(montantRemise))
                .build();

        LigneFactureEntity saved = ligneFactureRepository.save(nouvelleLigne);

        recalculerTotauxFacture(factureId);

        return saved;
    }

    @Override
    @Transactional
    public void deleteLigneFacture(Integer id) {
        LigneFactureEntity ligne = ligneFactureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne facture introuvable"));

        Integer factureId = ligne.getFacture().getId();

        ligneFactureRepository.delete(ligne);

        recalculerTotauxFacture(factureId);
    }

    /**
     * ✅ Recalcul total facture avec remise
     */
    private void recalculerTotauxFacture(Integer factureId) {
        FactureEntity facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture introuvable avec ID : " + factureId));

        List<LigneFactureEntity> lignes = ligneFactureRepository.findByFactureId(factureId);

        double totalHT = 0.0;
        double totalTVA = 0.0;
        double totalRemise = 0.0;

        for (LigneFactureEntity l : lignes) {
            totalHT += l.getTotalHT() != null ? l.getTotalHT() : 0.0;
            totalTVA += l.getTotalTVA() != null ? l.getTotalTVA() : 0.0;
            totalRemise += l.getMontantRemise() != null ? l.getMontantRemise() : 0.0;
        }

        facture.setTotalHT(arrondir(totalHT));
        facture.setTotalTVA(arrondir(totalTVA));
        facture.setTotalTTC(arrondir(totalHT + totalTVA));
        facture.setTotalRemise(arrondir(totalRemise));

        factureRepository.save(facture);
    }

    private double arrondir(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }
}