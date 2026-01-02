package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.AvoirEntity;
import com.example.AppPfa.DAO.Entity.RetourProduitEntity;
import com.example.AppPfa.Repository.RetourProduitRepository;
import com.example.AppPfa.Service.AvoirManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/avoirs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")  // 👈 CORS pour React
public class AvoirController {

    private final AvoirManager avoirManager;
    private final RetourProduitRepository retourProduitRepository;

    @PostMapping("/retour/{retourId}")
    public AvoirEntity creerAvoirDepuisRetour(@PathVariable Integer retourId) {
        RetourProduitEntity retour = retourProduitRepository.findById(retourId)
                .orElseThrow(() -> new RuntimeException("Retour introuvable : " + retourId));

        log.info("API - Création Avoir depuis Retour {}", retourId);
        return avoirManager.creerAvoirDepuisRetour(retour);
    }

    @PostMapping("/manuel")
    public AvoirEntity creerAvoirManuel(@RequestBody Map<String, Object> payload) {

        Integer clientId = parseInteger(payload.get("clientId"));
        Double montant = parseDouble(payload.get("montant"));
        String commentaire = payload.get("commentaire") != null
                ? payload.get("commentaire").toString()
                : null;

        if (clientId == null || montant == null || montant <= 0) {
            throw new RuntimeException("clientId et montant > 0 sont obligatoires");
        }

        log.info("API - Création Avoir Manuel | clientId={} | montant={}", clientId, montant);
        return avoirManager.creerAvoirManuel(clientId, montant, commentaire);
    }

    @PostMapping("/{avoirId}/utiliser")
    public AvoirEntity utiliserAvoir(@PathVariable Integer avoirId) {
        log.info("API - Utilisation Avoir {}", avoirId);
        return avoirManager.utiliserAvoir(avoirId);
    }

    @PostMapping("/{avoirId}/annuler")
    public AvoirEntity annulerAvoir(@PathVariable Integer avoirId) {
        log.info("API - Annulation Avoir {}", avoirId);
        return avoirManager.annulerAvoir(avoirId);
    }

    @GetMapping
    public List<AvoirEntity> getAllAvoirs() {
        return avoirManager.getAllAvoirs();
    }

    @GetMapping("/{id}")
    public AvoirEntity getAvoirById(@PathVariable Integer id) {
        return avoirManager.getAvoirById(id);
    }

    @GetMapping("/client/{clientId}")
    public List<AvoirEntity> getAvoirsByClient(@PathVariable Integer clientId) {
        return avoirManager.getAvoirsByClient(clientId);
    }

    @GetMapping("/fournisseur/{fournisseurId}")
    public List<AvoirEntity> getAvoirsByFournisseur(@PathVariable Integer fournisseurId) {
        return avoirManager.getAvoirsByFournisseur(fournisseurId);
    }

    @GetMapping("/client/{clientId}/disponibles")
    public List<AvoirEntity> getAvoirsDisponibles(@PathVariable Integer clientId) {
        return avoirManager.getAvoirsDisponibles(clientId);
    }

    @GetMapping("/client/{clientId}/solde")
    public Double getSoldeAvoirsClient(@PathVariable Integer clientId) {
        return avoirManager.getSoldeAvoirsClient(clientId);
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

    private Double parseDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return null;
        }
    }
}