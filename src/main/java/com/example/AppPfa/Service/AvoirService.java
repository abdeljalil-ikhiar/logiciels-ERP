package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.*;
import com.example.AppPfa.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AvoirService implements AvoirManager {

    private final AvoirRepository avoirRepository;
    private final LigneAvoirRepository ligneAvoirRepository;
    private final ClientRepository clientRepository;

    // ═══════════════════════════════════════════════════════════════
    // ✅ CRÉER AVOIR DEPUIS RETOUR
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public AvoirEntity creerAvoirDepuisRetour(RetourProduitEntity retour) {

        AvoirEntity.TypeAvoir typeAvoir =
                retour.getTypeRetour() == RetourProduitEntity.TypeRetour.RETOUR_CLIENT
                        ? AvoirEntity.TypeAvoir.AVOIR_CLIENT
                        : AvoirEntity.TypeAvoir.AVOIR_FOURNISSEUR;

        AvoirEntity avoir = AvoirEntity.builder()
                .numeroAvoir(generateNumeroAvoir())
                .dateAvoir(LocalDate.now())
                .typeAvoir(typeAvoir)
                .client(retour.getClient())
                .fournisseur(retour.getFournisseur())
                .retourProduit(retour)
                .statut(AvoirEntity.StatutAvoir.VALIDE)
                .commentaire("Avoir généré depuis retour " + retour.getNumeroRetour())
                .build();

        avoir = avoirRepository.save(avoir);

        List<LigneAvoirEntity> lignesAvoir = new ArrayList<>();
        Double totalHT = 0.0;
        Double totalTVA = 0.0;
        Double totalTTC = 0.0;

        for (LigneRetourEntity ligneRetour : retour.getLignesRetour()) {
            // ✅ Seulement REMBOURSEMENT (pas échange)
            if (ligneRetour.getActionRetour() == LigneRetourEntity.ActionRetour.REMBOURSEMENT) {

                LigneAvoirEntity ligneAvoir = LigneAvoirEntity.builder()
                        .avoir(avoir)
                        .produit(ligneRetour.getProduit())
                        .ligneRetour(ligneRetour)
                        .quantite(ligneRetour.getQuantiteRetournee())
                        .prixUnitaire(ligneRetour.getPrixUnitaire())
                        .totalHT(ligneRetour.getTotalHT())
                        .totalTVA(ligneRetour.getTotalTVA())
                        .totalTTC(ligneRetour.getTotalTTC())
                        .description("Retour: " + ligneRetour.getProduit().getDesignation())
                        .build();

                ligneAvoirRepository.save(ligneAvoir);
                lignesAvoir.add(ligneAvoir);

                totalHT += ligneRetour.getTotalHT();
                totalTVA += ligneRetour.getTotalTVA();
                totalTTC += ligneRetour.getTotalTTC();
            }
        }

        avoir.setLignesAvoir(lignesAvoir);
        avoir.setTotalHT(totalHT);
        avoir.setTotalTVA(totalTVA);
        avoir.setTotalTTC(totalTTC);

        log.info("✅ Avoir créé: {} | Type: {} | Total: {} TTC",
                avoir.getNumeroAvoir(), typeAvoir, totalTTC);

        return avoirRepository.save(avoir);
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ CRÉER AVOIR MANUEL
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public AvoirEntity creerAvoirManuel(Integer clientId, Double montant, String commentaire) {

        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable : " + clientId));

        AvoirEntity avoir = AvoirEntity.builder()
                .numeroAvoir(generateNumeroAvoir())
                .dateAvoir(LocalDate.now())
                .typeAvoir(AvoirEntity.TypeAvoir.AVOIR_CLIENT)
                .client(client)
                .totalHT(montant)
                .totalTVA(0.0)
                .totalTTC(montant)
                .statut(AvoirEntity.StatutAvoir.VALIDE)
                .commentaire(commentaire)
                .build();

        log.info("✅ Avoir manuel créé: {} | Client: {} | Montant: {}",
                avoir.getNumeroAvoir(), client.getNomclient(), montant);

        return avoirRepository.save(avoir);
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ UTILISER AVOIR
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public AvoirEntity utiliserAvoir(Integer avoirId) {
        AvoirEntity avoir = avoirRepository.findById(avoirId)
                .orElseThrow(() -> new RuntimeException("Avoir introuvable : " + avoirId));

        if (avoir.getStatut() != AvoirEntity.StatutAvoir.VALIDE) {
            throw new RuntimeException("Cet avoir n'est pas utilisable. Statut: " + avoir.getStatut());
        }

        avoir.setStatut(AvoirEntity.StatutAvoir.UTILISE);
        log.info("✅ Avoir utilisé: {}", avoir.getNumeroAvoir());

        return avoirRepository.save(avoir);
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ ANNULER AVOIR
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public AvoirEntity annulerAvoir(Integer avoirId) {
        AvoirEntity avoir = avoirRepository.findById(avoirId)
                .orElseThrow(() -> new RuntimeException("Avoir introuvable : " + avoirId));

        if (avoir.getStatut() == AvoirEntity.StatutAvoir.UTILISE) {
            throw new RuntimeException("Impossible d'annuler un avoir déjà utilisé");
        }

        avoir.setStatut(AvoirEntity.StatutAvoir.ANNULE);
        log.info("❌ Avoir annulé: {}", avoir.getNumeroAvoir());

        return avoirRepository.save(avoir);
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ GETTERS
    // ═══════════════════════════════════════════════════════════════

    @Override
    public List<AvoirEntity> getAllAvoirs() {
        return avoirRepository.findAll();
    }

    @Override
    public AvoirEntity getAvoirById(Integer id) {
        return avoirRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avoir introuvable : " + id));
    }

    @Override
    public List<AvoirEntity> getAvoirsByClient(Integer clientId) {
        return avoirRepository.findByClientIdOrderByDateAvoirDesc(clientId);
    }

    @Override
    public List<AvoirEntity> getAvoirsByFournisseur(Integer fournisseurId) {
        return avoirRepository.findByFournisseurIdOrderByDateAvoirDesc(fournisseurId);
    }

    @Override
    public List<AvoirEntity> getAvoirsDisponibles(Integer clientId) {
        // ✅ Correction : on filtre bien par client
        return avoirRepository.findAvoirsClientDisponibles(clientId);
    }

    @Override
    public Double getSoldeAvoirsClient(Integer clientId) {
        Double solde = avoirRepository.sumAvoirsDisponiblesByClient(clientId);
        return solde != null ? solde : 0.0;
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ MÉTHODES UTILITAIRES
    // ═══════════════════════════════════════════════════════════════

    private String generateNumeroAvoir() {
        Integer maxId = avoirRepository.findMaxId();
        int nextId = (maxId != null ? maxId : 0) + 1;
        return String.format("AV-%05d", nextId);
    }
}