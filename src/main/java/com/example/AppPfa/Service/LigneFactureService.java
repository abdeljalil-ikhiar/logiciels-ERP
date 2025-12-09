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

    /**
     * Récupérer une ligne de facture par ID
     */
    @Override
    @Transactional(readOnly = true)
    public LigneFactureEntity getLigneFactureById(Integer id) {
        return ligneFactureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne de facture introuvable avec ID: " + id));
    }

    /**
     * Récupérer toutes les lignes de facture
     */
    @Override
    @Transactional(readOnly = true)
    public List<LigneFactureEntity> getAllLignesFacture() {
        return ligneFactureRepository.findAll();
    }

    /**
     * Récupérer les lignes d’une facture
     */
    @Override
    @Transactional(readOnly = true)
    public List<LigneFactureEntity> getLignesByFactureId(Integer factureId) {
        return ligneFactureRepository.findByFactureId(factureId);
    }

    /**
     * Ajouter une ligne de facture à partir d’une ligne de bon de livraison
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

        // Calcul des totaux
        Double totalHT = ligneBonLivraison.getTotalHT();
        Double totalTVA = ligneBonLivraison.getTotalTVA();
        Double totalTTC = ligneBonLivraison.getTotalTTC();

        // Construction de la ligne de facture
        LigneFactureEntity nouvelleLigne = LigneFactureEntity.builder()
                .facture(facture)
                .ligneBonLivraison(ligneBonLivraison)
                .ligneCommande(ligneCommande)
                .totalHT(totalHT)
                .totalTVA(totalTVA)
                .totalTTC(totalTTC)
                .build();

        // Enregistrement
        LigneFactureEntity saved = ligneFactureRepository.save(nouvelleLigne);

        // Recalcul de la facture
        recalculerTotauxFacture(factureId);

        return saved;
    }

    /**
     * Supprimer une ligne de facture
     */
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
     * Recalcul total facture
     */
    private void recalculerTotauxFacture(Integer factureId) {

        FactureEntity facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture introuvable avec ID : " + factureId));

        List<LigneFactureEntity> lignes = ligneFactureRepository.findByFactureId(factureId);

        double totalHT = 0.0;
        double totalTVA = 0.0;

        for (LigneFactureEntity l : lignes) {
            totalHT += l.getTotalHT();
            totalTVA += l.getTotalTVA();
        }

        facture.setTotalHT(totalHT);
        facture.setTotalTVA(totalTVA);
        facture.setTotalTTC(totalHT + totalTVA);

        factureRepository.save(facture);
    }
}
