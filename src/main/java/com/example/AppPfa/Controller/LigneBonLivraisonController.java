package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.LigneBonLivraisonEntity;
import com.example.AppPfa.Service.LigneBonLivraisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ligne-bon-livraison")
@CrossOrigin(origins = "http://localhost:3000")
public class LigneBonLivraisonController {

   @Autowired
   private LigneBonLivraisonService ligneBonLivraisonService;

    // ✅ 1️⃣ Récupérer une ligne par ID
    @GetMapping("/{id}")
    public ResponseEntity<LigneBonLivraisonEntity> getLigneById(@PathVariable Integer id) {
        return ResponseEntity.ok(ligneBonLivraisonService.getLigneBonLivraisonById(id));
    }

    // ✅ 2️⃣ Récupérer toutes les lignes
    @GetMapping
    public ResponseEntity<List<LigneBonLivraisonEntity>> getAllLignes() {
        return ResponseEntity.ok(ligneBonLivraisonService.getLigneBonLivraison());
    }

    // ✅ 3️⃣ Récupérer toutes les lignes d’un bon de livraison
    @GetMapping("/bon-livraison/{idBonLivraison}")
    public ResponseEntity<List<LigneBonLivraisonEntity>> getLignesByBonLivraison(@PathVariable Integer idBonLivraison) {
        return ResponseEntity.ok(ligneBonLivraisonService.getLigneBonLivraisonByBonLivraison(idBonLivraison));
    }

    // ✅ 4️⃣ Supprimer une ligne
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLigne(@PathVariable Integer id) {
        ligneBonLivraisonService.deleteLigneBonLivraison(id);
        return ResponseEntity.ok("Ligne supprimée avec succès ✅");
    }
}
