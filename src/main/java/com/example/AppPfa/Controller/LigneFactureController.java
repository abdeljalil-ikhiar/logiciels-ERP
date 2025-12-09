package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.LigneFactureEntity;
import com.example.AppPfa.Service.LigneFactureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ligne-facture")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class LigneFactureController {

    private final LigneFactureService ligneFactureService;

    // 1️⃣ Récupérer toutes les lignes de facture
    @GetMapping
    public ResponseEntity<List<LigneFactureEntity>> getAllLignesFacture() {
        return ResponseEntity.ok(ligneFactureService.getAllLignesFacture());
    }

    // 2️⃣ Récupérer une ligne de facture par ID
    @GetMapping("/{id}")
    public ResponseEntity<LigneFactureEntity> getLigneFactureById(@PathVariable Integer id) {
        return ResponseEntity.ok(ligneFactureService.getLigneFactureById(id));
    }

    // 3️⃣ Récupérer toutes les lignes d’une facture
    @GetMapping("/facture/{factureId}")
    public ResponseEntity<List<LigneFactureEntity>> getLignesByFacture(@PathVariable Integer factureId) {
        return ResponseEntity.ok(ligneFactureService.getLignesByFactureId(factureId));
    }

    // 4️⃣ Ajouter une ligne de facture à partir d’une ligne de bon de livraison
    @PostMapping("/add/{factureId}/{ligneBonLivraisonId}")
    public ResponseEntity<LigneFactureEntity> addLigneFacture(
            @PathVariable Integer factureId,
            @PathVariable Integer ligneBonLivraisonId
    ) {
        return ResponseEntity.ok(ligneFactureService.addLigneFacture(factureId, ligneBonLivraisonId));
    }

    // 5️⃣ Supprimer une ligne de facture
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLigneFacture(@PathVariable Integer id) {
        ligneFactureService.deleteLigneFacture(id);
        return ResponseEntity.ok("Ligne de facture supprimée avec succès ✅");
    }
}
