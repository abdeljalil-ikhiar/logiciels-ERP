package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.FactureEntity;
import com.example.AppPfa.Service.FactureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factures")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;

    // Créer une facture
    @PostMapping("/create")
    public ResponseEntity<FactureEntity> addFacture(@RequestBody FactureEntity facture) {
        return ResponseEntity.ok(factureService.addFacture(facture));
    }

    // Mettre à jour une facture
    @PutMapping("/update/{id}")
    public ResponseEntity<FactureEntity> updateFacture(
            @PathVariable int id,
            @RequestBody FactureEntity facture
    ) {
        return ResponseEntity.ok(factureService.updateFactue(id, facture));
    }

    // Récupérer toutes les factures
    @GetMapping
    public ResponseEntity<List<FactureEntity>> getAllFactures() {
        return ResponseEntity.ok(factureService.getAllFacture());
    }

    // Récupérer une facture par ID
    @GetMapping("/{id}")
    public ResponseEntity<FactureEntity> getFactureById(@PathVariable int id) {
        return ResponseEntity.ok(factureService.getAllFacture()
                .stream()
                .filter(f -> f.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Facture introuvable avec l'ID: " + id)));
    }

    // Supprimer une facture
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFacture(@PathVariable int id) {
        factureService.deleteFacture(id);
        return ResponseEntity.ok("Facture supprimée avec succès");
    }
}
