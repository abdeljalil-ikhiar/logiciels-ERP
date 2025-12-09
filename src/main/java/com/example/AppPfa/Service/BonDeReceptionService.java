package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.BonDeReceptionEntity;
import com.example.AppPfa.DAO.Entity.LigneBonDeReceptionEntities;
import com.example.AppPfa.Repository.BonDeReceptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class BonDeReceptionService implements BonDeReceptionManager {


    @Autowired
    private BonDeReceptionRepository bonDeReceptionRepository;

    private static final String PREFIX = "BR-";
    private static final String DATE_FORMAT = "yyyyMMdd";

    // ➕ AJOUT Bon de réception (avec numéro auto)
    @Override
    public BonDeReceptionEntity addBonReception(BonDeReceptionEntity bon) {

        // Génération auto du numéro si vide
        if (bon.getNumeroreception() == null || bon.getNumeroreception().isBlank()) {
            bon.setNumeroreception(generateNumeroReception());
        }

        // Mettre date si vide
        if (bon.getDate() == null) {
            bon.setDate(LocalDate.now());
        }

        // Sync relation bidirectionnelle
        if (bon.getLigneBonDeReceptionEntities() != null) {
            for (LigneBonDeReceptionEntities ligne : bon.getLigneBonDeReceptionEntities()) {
                ligne.setBonDeReceptionEntity(bon);
            }
        }

        return bonDeReceptionRepository.save(bon);
    }

    // 🔄 UPDATE Bon de réception
    @Override
    public BonDeReceptionEntity updateBonReception(int id, BonDeReceptionEntity bonDeReceptionEntity) {
        BonDeReceptionEntity existing = bonDeReceptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bon de réception introuvable : " + id));

        existing.setDate(bonDeReceptionEntity.getDate());
        existing.setCommandeAchatsEntity(bonDeReceptionEntity.getCommandeAchatsEntity());

        // Remplacer les lignes
        existing.getLigneBonDeReceptionEntities().clear();
        if (bonDeReceptionEntity.getLigneBonDeReceptionEntities() != null) {
            for (LigneBonDeReceptionEntities ligne : bonDeReceptionEntity.getLigneBonDeReceptionEntities()) {
                ligne.setBonDeReceptionEntity(existing);
                existing.getLigneBonDeReceptionEntities().add(ligne);
            }
        }

        return bonDeReceptionRepository.save(existing);
    }

    // 📌 Liste
    @Override
    public List<BonDeReceptionEntity> getAllBonReception() {
        return bonDeReceptionRepository.findAll();
    }

    // 🔍 ID
    @Override
    public BonDeReceptionEntity getBonReceptionById(int id) {
        return bonDeReceptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bon de réception introuvable : " + id));
    }

    // ❌ DELETE
    @Override
    public void deleteBonReception(int id) {
        if (!bonDeReceptionRepository.existsById(id)) {
            throw new RuntimeException("Bon de réception introuvable : " + id);
        }
        bonDeReceptionRepository.deleteById(id);
    }

    // 🔥 Fonction Génération Numéro BR automatique
    private String generateNumeroReception() {
        LocalDate today = LocalDate.now();
        String datePart = today.format(DateTimeFormatter.ofPattern(DATE_FORMAT));

        String prefixDate = PREFIX + datePart; // BR-20251207

        // Compter combien de bons déjà créés aujourd'hui
        long count = bonDeReceptionRepository.countByNumeroreceptionStartingWith(prefixDate) + 1;

        // Format : BR-20251207-0001
        return prefixDate + "-" + String.format("%04d", count);
    }


}
