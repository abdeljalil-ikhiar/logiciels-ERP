// src/main/java/com/example/AppPfa/Controller/StockController.java

package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.EtatduStock;
import com.example.AppPfa.DAO.Entity.ProduitEntity;
import com.example.AppPfa.Repository.EtatduStockRepository;
import com.example.AppPfa.Repository.ProduitRepository;
import com.example.AppPfa.Service.StockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class StockController {

    private final StockManager stockManager;
    private final EtatduStockRepository etatduStockRepository;
    private final ProduitRepository produitRepository;

    // GET ALL STOCKS
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStocks() {
        log.info("GET /api/stocks");
        try {
            List<ProduitEntity> produits = produitRepository.findAll();
            List<Map<String, Object>> stocksList = new ArrayList<>();

            for (ProduitEntity produit : produits) {
                stocksList.add(buildStockItem(produit));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stocks", stocksList);
            response.put("total", stocksList.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur getAllStocks: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur serveur: " + e.getMessage()));
        }
    }

    // GET STOCK BY PRODUIT ID
    @GetMapping("/produit/{produitId}")
    public ResponseEntity<Map<String, Object>> getStockByProduitId(@PathVariable Integer produitId) {
        log.info("GET /api/stocks/produit/{}", produitId);
        try {
            ProduitEntity produit = produitRepository.findById(produitId)
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé: " + produitId));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.putAll(buildStockItem(produit));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur getStockByProduitId: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    // CRÉER/MODIFIER SEUILS
    @PostMapping("/seuils/{produitId}")
    public ResponseEntity<Map<String, Object>> creerSeuils(
            @PathVariable Integer produitId,
            @RequestBody Map<String, Object> request) {

        log.info("POST /api/stocks/seuils/{}", produitId);
        try {
            ProduitEntity produit = produitRepository.findById(produitId)
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé: " + produitId));

            EtatduStock etat = etatduStockRepository.findByProduitId(produitId)
                    .orElseGet(() -> {
                        EtatduStock newEtat = new EtatduStock();
                        newEtat.setProduit(produit);
                        newEtat.setStockReel(0.0);
                        return newEtat;
                    });

            if (request.containsKey("stockMin")) {
                etat.setStockMin(getDouble(request, "stockMin"));
            }
            if (request.containsKey("stockMax")) {
                etat.setStockMax(getDouble(request, "stockMax"));
            }
            if (request.containsKey("zoneStock")) {
                etat.setZoneStock((String) request.get("zoneStock"));
            }

            etatduStockRepository.save(etat);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Seuils mis à jour avec succès");
            response.putAll(buildStockItem(produit));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur creerSeuils: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    // SUPPRIMER SEUILS
    @DeleteMapping("/seuils/{produitId}")
    public ResponseEntity<Map<String, Object>> supprimerSeuils(@PathVariable Integer produitId) {
        log.info("DELETE /api/stocks/seuils/{}", produitId);
        try {
            EtatduStock etat = etatduStockRepository.findByProduitId(produitId)
                    .orElseThrow(() -> new RuntimeException("Seuils non trouvés pour produit: " + produitId));

            etat.setStockMin(0.0);
            etat.setStockMax(0.0);
            etat.setZoneStock(null);
            etatduStockRepository.save(etat);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Seuils supprimés avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur supprimerSeuils: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    // MÉTHODE PRINCIPALE CORRIGÉE : buildStockItem
    private Map<String, Object> buildStockItem(ProduitEntity produit) {
        Map<String, Object> item = new HashMap<>();

        item.put("produitId", produit.getId());
        item.put("reference", produit.getReferences());
        item.put("designation", produit.getDesignation());

        // Infos produit imbriquées
        Map<String, Object> produitInfo = new HashMap<>();
        produitInfo.put("id", produit.getId());
        produitInfo.put("references", produit.getReferences());
        produitInfo.put("designation", produit.getDesignation());
        item.put("produit", produitInfo);

        // LE VRAI STOCK CALCULÉ EN TEMPS RÉEL
        Double vraiStock = stockManager.getStockReel(produit.getId());

        EtatduStock etat = etatduStockRepository.findByProduitId(produit.getId()).orElse(null);

        // Stock actuel (le bon !)
        item.put("stockActuel", vraiStock != null ? vraiStock : 0.0);

        // Seuils
        if (etat != null) {
            item.put("stockMin", etat.getStockMin() != null ? etat.getStockMin() : 0.0);
            item.put("stockMax", etat.getStockMax() != null ? etat.getStockMax() : 0.0);
            item.put("zoneStock", etat.getZoneStock());
            item.put("seuilsConfigure", etat.getStockMin() > 0 || etat.getStockMax() > 0);
        } else {
            item.put("stockMin", 0.0);
            item.put("stockMax", 0.0);
            item.put("zoneStock", null);
            item.put("seuilsConfigure", false);
        }

        // Calcul du statut propre (sans emojis)
        Double min = etat != null ? etat.getStockMin() : 0.0;
        Double max = etat != null ? etat.getStockMax() : 0.0;
        String statutCode = calculerStatutCode(vraiStock, min, max);

        item.put("statutCode", statutCode);
        item.put("statut", switch (statutCode) {
            case "RUPTURE" -> "Rupture de stock";
            case "CRITIQUE" -> "Stock critique";
            case "FAIBLE" -> "Stock faible";
            case "SURSTOCK" -> "Surstock";
            case "NORMAL" -> "Stock normal";
            default -> "Non configuré";
        });

        return item;
    }

    // Calcul du code statut (fiable à 100%)
    private String calculerStatutCode(Double actuel, Double min, Double max) {
        if (actuel == null) actuel = 0.0;
        if (min == null || min <= 0) min = 0.0;
        if (max == null || max <= 0) max = 0.0;

        if (min == 0 && max == 0) return "NON_CONFIGURE";
        if (actuel <= 0) return "RUPTURE";
        if (min > 0 && actuel <= min * 0.5) return "CRITIQUE";
        if (min > 0 && actuel <= min) return "FAIBLE";
        if (max > 0 && actuel > max) return "SURSTOCK";
        return "NORMAL";
    }

    // Utilitaires
    private Double getDouble(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return 0.0;
        if (value instanceof Number n) return n.doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        return error;
    }
}