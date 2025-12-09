package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.BonDeLivraisonFournisseurEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface BonDeLivraisonFournisseurManager {

    // ✅ CRUD de base
    BonDeLivraisonFournisseurEntity creerBonLivraison(Integer idBonReception);

    BonDeLivraisonFournisseurEntity creerBonLivraison(
            Integer idBonReception,
            String numeroBLFournisseur,
            LocalDate dateLivraison,
            LocalDate dateBLFournisseur,
            String commentaire
    );

    BonDeLivraisonFournisseurEntity recalculerBonLivraison(Integer idBonReception);

    BonDeLivraisonFournisseurEntity getBonLivraison(Integer id);

    BonDeLivraisonFournisseurEntity getBonLivraisonByBonReception(Integer idBonReception);

    List<BonDeLivraisonFournisseurEntity> getBonLivraisonByFournisseur(Integer idFournisseur);

    List<BonDeLivraisonFournisseurEntity> getAllBonLivraison();

    void deleteBonLivraison(Integer id);

    // ✅ Gestion PDF
    void uploadPdf(Integer id, MultipartFile file) throws Exception;

    byte[] getPdfFile(Integer id);

    void deletePdf(Integer id);
}