package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.*;
import com.example.AppPfa.Repository.BonDeLivraisonFournisseurRepository;
import com.example.AppPfa.Repository.BonDeReceptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BonDeLivraisonFournisseurService implements BonDeLivraisonFournisseurManager {

    @Autowired
    private BonDeLivraisonFournisseurRepository bonDeLivraisonRepository;

    @Autowired
    private BonDeReceptionRepository bonDeReceptionRepository;

    @Autowired
    private LigneBonDeLivraisonFournisseurService ligneBonDeLivraisonService;

    // =============================================
    // ✅ CRÉATION
    // =============================================

    @Override
    @Transactional
    public BonDeLivraisonFournisseurEntity creerBonLivraison(Integer idBonReception) {
        return creerBonLivraison(idBonReception, null, null, null, null);
    }

    @Override
    @Transactional
    public BonDeLivraisonFournisseurEntity creerBonLivraison(
            Integer idBonReception,
            String numeroBLFournisseur,
            LocalDate dateLivraison,
            LocalDate dateBLFournisseur,
            String commentaire) {

        System.out.println("📦 Création BL Fournisseur pour BR ID: " + idBonReception);

        // 1️⃣ Récupérer le bon de réception
        BonDeReceptionEntity bonReception = bonDeReceptionRepository.findById(idBonReception)
                .orElseThrow(() -> new RuntimeException("Bon de réception non trouvé avec l'ID: " + idBonReception));

        // 2️⃣ Vérifier si un BL existe déjà
        if (bonDeLivraisonRepository.existsByBonDeReceptionEntityId(idBonReception)) {
            throw new RuntimeException("Un bon de livraison existe déjà pour ce bon de réception");
        }

        // 3️⃣ Récupérer la commande d'achat associée
        CommandeAchatsEntity commande = bonReception.getCommandeAchatsEntity();
        if (commande == null) {
            throw new RuntimeException("Aucune commande associée au bon de réception");
        }

        // 4️⃣ Générer un numéro interne
        String numeroBonLivraison = genererNumeroBonLivraison();

        // 5️⃣ Construire le BL fournisseur
        BonDeLivraisonFournisseurEntity bonLivraison = new BonDeLivraisonFournisseurEntity();
        bonLivraison.setNumeroLivraison(numeroBonLivraison);
        bonLivraison.setDateLivraison(dateLivraison != null ? dateLivraison : LocalDate.now());
        bonLivraison.setNumeroBLFournisseur(numeroBLFournisseur);
        bonLivraison.setDateBLFournisseur(dateBLFournisseur);
        bonLivraison.setCommentaire(commentaire);
        bonLivraison.setBonDeReceptionEntity(bonReception);
        bonLivraison.setFournisseurEntity(commande.getFournisseurEntity());
        bonLivraison.setLigneBonDeLivraisonEntities(new ArrayList<>());
        bonLivraison.setTotalHT(0.0);
        bonLivraison.setTotalTVA(0.0);
        bonLivraison.setTotalTTC(0.0);
        bonLivraison.setStatut(BonDeLivraisonFournisseurEntity.StatutLivraison.EN_ATTENTE);

        // 6️⃣ Générer les lignes
        double totalHT = 0.0;
        double totalTVA = 0.0;

        for (LigneBonDeReceptionEntities ligneBonReception : bonReception.getLigneBonDeReceptionEntities()) {
            LigneCommandeAchatsEntity ligneCommande = ligneBonReception.getLigneCommandeAchatsEntity();
            if (ligneCommande == null) continue;

            Double prixUnitaire = ligneCommande.getPrixUnitaire();
            Double quantiteReception = ligneBonDeLivraisonService.parseQuantite(ligneBonReception.getQtereception());
            Double tauxTVA = 20.0;

            Double ligneTotalHT = ligneBonDeLivraisonService.arrondir(prixUnitaire * quantiteReception);
            Double ligneTotalTVA = ligneBonDeLivraisonService.arrondir(ligneTotalHT * (tauxTVA / 100.0));
            Double ligneTotalTTC = ligneBonDeLivraisonService.arrondir(ligneTotalHT + ligneTotalTVA);

            LigneBonDeLivraisonFournisseurEntity ligneBL = LigneBonDeLivraisonFournisseurEntity.builder()
                    .bonDeLivraisonFournisseurEntity(bonLivraison)
                    .ligneBonDeReceptionEntity(ligneBonReception)
                    .ligneCommandeAchatsEntity(ligneCommande)
                    .totalHT(ligneTotalHT)
                    .totalTVA(ligneTotalTVA)
                    .totalTTC(ligneTotalTTC)
                    .build();

            bonLivraison.getLigneBonDeLivraisonEntities().add(ligneBL);
            totalHT += ligneTotalHT;
            totalTVA += ligneTotalTVA;
        }

        // 7️⃣ Totaux globaux
        bonLivraison.setTotalHT(ligneBonDeLivraisonService.arrondir(totalHT));
        bonLivraison.setTotalTVA(ligneBonDeLivraisonService.arrondir(totalTVA));
        bonLivraison.setTotalTTC(ligneBonDeLivraisonService.arrondir(totalHT + totalTVA));
        bonLivraison.setStatut(calculerStatut(bonLivraison.getLigneBonDeLivraisonEntities()));

        System.out.println("✅ BL Fournisseur créé: " + numeroBonLivraison);
        return bonDeLivraisonRepository.save(bonLivraison);
    }

    // =============================================
    // ✅ RECALCUL
    // =============================================

    @Override
    @Transactional
    public BonDeLivraisonFournisseurEntity recalculerBonLivraison(Integer idBonReception) {
        BonDeLivraisonFournisseurEntity bonLivraison = bonDeLivraisonRepository
                .findByBonDeReceptionEntityId(idBonReception)
                .orElse(null);

        if (bonLivraison == null) return null;

        double totalHT = 0.0;
        double totalTVA = 0.0;

        for (LigneBonDeLivraisonFournisseurEntity ligne : bonLivraison.getLigneBonDeLivraisonEntities()) {
            LigneCommandeAchatsEntity ligneCommande = ligne.getLigneCommandeAchatsEntity();
            LigneBonDeReceptionEntities ligneBonReception = ligne.getLigneBonDeReceptionEntity();

            Double prixUnitaire = ligneCommande.getPrixUnitaire();
            Double quantiteReception = ligneBonDeLivraisonService.parseQuantite(ligneBonReception.getQtereception());
            Double tauxTVA = 20.0;

            Double ligneTotalHT = ligneBonDeLivraisonService.arrondir(prixUnitaire * quantiteReception);
            Double ligneTotalTVA = ligneBonDeLivraisonService.arrondir(ligneTotalHT * (tauxTVA / 100.0));
            Double ligneTotalTTC = ligneBonDeLivraisonService.arrondir(ligneTotalHT + ligneTotalTVA);

            ligne.setTotalHT(ligneTotalHT);
            ligne.setTotalTVA(ligneTotalTVA);
            ligne.setTotalTTC(ligneTotalTTC);

            totalHT += ligneTotalHT;
            totalTVA += ligneTotalTVA;
        }

        bonLivraison.setTotalHT(ligneBonDeLivraisonService.arrondir(totalHT));
        bonLivraison.setTotalTVA(ligneBonDeLivraisonService.arrondir(totalTVA));
        bonLivraison.setTotalTTC(ligneBonDeLivraisonService.arrondir(totalHT + totalTVA));
        bonLivraison.setStatut(calculerStatut(bonLivraison.getLigneBonDeLivraisonEntities()));

        return bonDeLivraisonRepository.save(bonLivraison);
    }

    // =============================================
    // ✅ LECTURE
    // =============================================

    @Override
    @Transactional(readOnly = true)
    public BonDeLivraisonFournisseurEntity getBonLivraison(Integer id) {
        return bonDeLivraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bon de livraison non trouvé avec l'ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public BonDeLivraisonFournisseurEntity getBonLivraisonByBonReception(Integer idBonReception) {
        return bonDeLivraisonRepository.findByBonDeReceptionEntityId(idBonReception)
                .orElseThrow(() -> new RuntimeException("Aucun BL trouvé pour le BR: " + idBonReception));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BonDeLivraisonFournisseurEntity> getBonLivraisonByFournisseur(Integer idFournisseur) {
        return bonDeLivraisonRepository.findByFournisseurEntityId(idFournisseur);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BonDeLivraisonFournisseurEntity> getAllBonLivraison() {
        System.out.println("📋 Récupération de tous les BL Fournisseurs");
        return bonDeLivraisonRepository.findAll();
    }

    // =============================================
    // ✅ SUPPRESSION
    // =============================================

    @Override
    @Transactional
    public void deleteBonLivraison(Integer id) {
        if (!bonDeLivraisonRepository.existsById(id)) {
            throw new RuntimeException("Bon de livraison non trouvé avec l'ID: " + id);
        }
        bonDeLivraisonRepository.deleteById(id);
        System.out.println("🗑️ BL Fournisseur supprimé: " + id);
    }

    // =============================================
    // ✅ GESTION PDF
    // =============================================

    @Override
    @Transactional
    public void uploadPdf(Integer id, MultipartFile file) throws Exception {
        System.out.println("📥 Upload PDF pour BL ID: " + id);
        System.out.println("📄 Fichier: " + file.getOriginalFilename() + " (" + file.getSize() + " bytes)");

        BonDeLivraisonFournisseurEntity bl = getBonLivraison(id);

        bl.setPdfFile(file.getBytes());
        bl.setPdfFileName(file.getOriginalFilename());
        bl.setPdfUploadDate(LocalDateTime.now());

        bonDeLivraisonRepository.save(bl);
        System.out.println("✅ PDF sauvegardé pour BL " + id);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getPdfFile(Integer id) {
        BonDeLivraisonFournisseurEntity bl = getBonLivraison(id);
        return bl.getPdfFile();
    }

    @Override
    @Transactional
    public void deletePdf(Integer id) {
        BonDeLivraisonFournisseurEntity bl = getBonLivraison(id);

        if (!bl.hasPdf()) {
            throw new RuntimeException("Aucun PDF attaché à ce bon de livraison");
        }

        bl.setPdfFile(null);
        bl.setPdfFileName(null);
        bl.setPdfUploadDate(null);

        bonDeLivraisonRepository.save(bl);
        System.out.println("🗑️ PDF supprimé pour BL " + id);
    }

    // =============================================
    // ✅ MÉTHODES PRIVÉES
    // =============================================

    private BonDeLivraisonFournisseurEntity.StatutLivraison calculerStatut(
            List<LigneBonDeLivraisonFournisseurEntity> lignes) {

        if (lignes.isEmpty()) {
            return BonDeLivraisonFournisseurEntity.StatutLivraison.EN_ATTENTE;
        }

        boolean toutLivre = true;

        for (LigneBonDeLivraisonFournisseurEntity ligne : lignes) {
            Double qteCommandee = ligne.getLigneCommandeAchatsEntity().getQuantite();
            Double qteLivree = ligneBonDeLivraisonService.parseQuantite(
                    ligne.getLigneBonDeReceptionEntity().getQtereception()
            );

            if (!qteLivree.equals(qteCommandee)) {
                toutLivre = false;
                break;
            }
        }

        return toutLivre
                ? BonDeLivraisonFournisseurEntity.StatutLivraison.COMPLETEMENT_LIVRE
                : BonDeLivraisonFournisseurEntity.StatutLivraison.PARTIELLEMENT_LIVRE;
    }

    private String genererNumeroBonLivraison() {
        long count = bonDeLivraisonRepository.count();
        return String.format("BLF-%d-%05d", LocalDate.now().getYear(), count + 1);
    }
}