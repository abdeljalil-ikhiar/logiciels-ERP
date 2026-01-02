package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.RetourProduitEntity;
import com.example.AppPfa.Service.RetourManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/retours")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")  // 👈 CORS pour React
public class RetourController {

    private final RetourManager retourManager;

    @PostMapping("/client")
    @SuppressWarnings("unchecked")
    public RetourProduitEntity creerRetourClient(@RequestBody Map<String, Object> payload) {

        Integer clientId = parseInteger(payload.get("clientId"));
        Integer bonLivraisonId = parseInteger(payload.get("bonLivraisonId"));
        String motif = payload.get("motif") != null ? payload.get("motif").toString() : null;

        List<Map<String, Object>> lignes =
                (List<Map<String, Object>>) payload.get("lignes");

        if (clientId == null || bonLivraisonId == null || lignes == null || lignes.isEmpty()) {
            throw new RuntimeException("clientId, bonLivraisonId et lignes sont obligatoires");
        }

        log.info("API - Création Retour Client | clientId={} | blId={}", clientId, bonLivraisonId);

        return retourManager.creerRetourClient(clientId, bonLivraisonId, lignes, motif);
    }

    @PostMapping("/fournisseur")
    @SuppressWarnings("unchecked")
    public RetourProduitEntity creerRetourFournisseur(@RequestBody Map<String, Object> payload) {

        Integer fournisseurId = parseInteger(payload.get("fournisseurId"));
        Integer bonReceptionId = parseInteger(payload.get("bonReceptionId"));
        String motif = payload.get("motif") != null ? payload.get("motif").toString() : null;

        List<Map<String, Object>> lignes =
                (List<Map<String, Object>>) payload.get("lignes");

        if (fournisseurId == null || bonReceptionId == null || lignes == null || lignes.isEmpty()) {
            throw new RuntimeException("fournisseurId, bonReceptionId et lignes sont obligatoires");
        }

        log.info("API - Création Retour Fournisseur | fournisseurId={} | brId={}", fournisseurId, bonReceptionId);

        return retourManager.creerRetourFournisseur(fournisseurId, bonReceptionId, lignes, motif);
    }

    @PostMapping("/{retourId}/valider")
    public RetourProduitEntity validerRetour(
            @PathVariable Integer retourId,
            @RequestParam(name = "genererAvoir", defaultValue = "true") boolean genererAvoir
    ) {
        log.info("API - Validation Retour {} (genererAvoir={})", retourId, genererAvoir);
        return retourManager.validerRetour(retourId, genererAvoir);
    }

    @PostMapping("/{retourId}/annuler")
    public RetourProduitEntity annulerRetour(@PathVariable Integer retourId) {
        log.info("API - Annulation Retour {}", retourId);
        return retourManager.annulerRetour(retourId);
    }

    @GetMapping
    public List<RetourProduitEntity> getAllRetours() {
        return retourManager.getAllRetours();
    }

    @GetMapping("/{id}")
    public RetourProduitEntity getRetourById(@PathVariable Integer id) {
        return retourManager.getRetourById(id);
    }

    private Integer parseInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return null;
        }
    }
}