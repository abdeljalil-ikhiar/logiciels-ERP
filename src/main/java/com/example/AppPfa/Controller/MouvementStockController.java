package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.MouvementStockEntity;
import com.example.AppPfa.Repository.MouvementStockRepository;
import com.example.AppPfa.Service.StockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mouvements-stock")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class MouvementStockController {

    private final MouvementStockRepository mouvementStockRepository;
    private final StockManager stockManager;

    // ═══════════════════════════════════════════════════════════════
    // ✅ GET ALL MOUVEMENTS
    // ═══════════════════════════════════════════════════════════════

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllMouvements() {
        log.info("📋 GET /api/mouvements-stock");
        List<MouvementStockEntity> mouvements = mouvementStockRepository.findAll();
        List<Map<String, Object>> result = mouvements.stream()
                .map(this::mapMouvementToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ GET MOUVEMENTS BY PRODUIT
    // ═══════════════════════════════════════════════════════════════

    @GetMapping("/produit/{produitId}")
    public ResponseEntity<List<Map<String, Object>>> getMouvementsByProduit(@PathVariable Integer produitId) {
        log.info("📋 GET /api/mouvements-stock/produit/{}", produitId);
        List<MouvementStockEntity> mouvements = mouvementStockRepository
                .findByProduitIdOrderByDateMouvementDesc(produitId);
        List<Map<String, Object>> result = mouvements.stream()
                .map(this::mapMouvementToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ GET MOUVEMENTS BY TYPE
    // ═══════════════════════════════════════════════════════════════

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Map<String, Object>>> getMouvementsByType(
            @PathVariable String type) {
        log.info("📋 GET /api/mouvements-stock/type/{}", type);
        try {
            MouvementStockEntity.TypeMouvement typeMouvement =
                    MouvementStockEntity.TypeMouvement.valueOf(type);
            List<MouvementStockEntity> mouvements = mouvementStockRepository
                    .findByTypeMouvementOrderByDateMouvementDesc(typeMouvement);
            List<Map<String, Object>> result = mouvements.stream()
                    .map(this::mapMouvementToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ RECHERCHE MOUVEMENTS
    // ═══════════════════════════════════════════════════════════════

    @GetMapping("/recherche")
    public ResponseEntity<List<Map<String, Object>>> rechercheMouvements(
            @RequestParam String reference) {
        log.info("🔍 GET /api/mouvements-stock/recherche?reference={}", reference);
        List<MouvementStockEntity> mouvements = mouvementStockRepository.findAll().stream()
                .filter(m -> m.getReference() != null &&
                        m.getReference().toLowerCase().contains(reference.toLowerCase()))
                .collect(Collectors.toList());
        List<Map<String, Object>> result = mouvements.stream()
                .map(this::mapMouvementToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ GET STOCK ACTUEL
    // ═══════════════════════════════════════════════════════════════

    @GetMapping("/stock-actuel/{produitId}")
    public ResponseEntity<Map<String, Object>> getStockActuel(@PathVariable Integer produitId) {
        log.info("📦 GET /api/mouvements-stock/stock-actuel/{}", produitId);
        Double stock = stockManager.getStockReel(produitId);

        Map<String, Object> response = new HashMap<>();
        response.put("produitId", produitId);
        response.put("stockActuel", stock);
        response.put("stockBas", stockManager.isStockBas(produitId));
        response.put("stockCritique", stockManager.isStockCritique(produitId));

        return ResponseEntity.ok(response);
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ HELPER
    // ═══════════════════════════════════════════════════════════════

    private Map<String, Object> mapMouvementToDTO(MouvementStockEntity mouvement) {
        Map<String, Object> dto = new HashMap<>();

        dto.put("id", mouvement.getId());
        dto.put("reference", mouvement.getReference());
        dto.put("typeMouvement", mouvement.getTypeMouvement() != null ?
                mouvement.getTypeMouvement().name() : null);
        dto.put("quantite", mouvement.getQuantite());
        dto.put("quantiteAvant", mouvement.getQuantiteAvant());
        dto.put("quantiteApres", mouvement.getQuantiteApres());
        dto.put("motif", mouvement.getMotif());
        dto.put("dateMouvement", mouvement.getDateMouvement() != null ?
                mouvement.getDateMouvement().toString() : null);
        dto.put("statut", mouvement.getStatut() != null ?
                mouvement.getStatut().name() : null);

        if (mouvement.getProduit() != null) {
            dto.put("produitId", mouvement.getProduit().getId());
            dto.put("produitRef", mouvement.getProduit().getReferences());
            dto.put("produitDesignation", mouvement.getProduit().getDesignation());
        }

        return dto;
    }
}