package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.BonLivraisonEntity;
import com.example.AppPfa.Service.BonLivraisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bon-livraison")
@CrossOrigin(origins = "http://localhost:3000")
public class BonLivraisonController {

   @Autowired
   private BonLivraisonService bonLivraisonService;

    // ✅ 1️⃣ Créer un bon de livraison à partir d’un bon de sortie
    @PostMapping("/create/{idBonSortie}")
    public ResponseEntity<BonLivraisonEntity> creerBonLivraison(@PathVariable Integer idBonSortie) {
        BonLivraisonEntity bonLivraison = bonLivraisonService.creerBonLivraison(idBonSortie);
        return ResponseEntity.ok(bonLivraison);
    }

    // ✅ 2️⃣ Récupérer un bon de livraison par ID
    @GetMapping("/{id}")
    public ResponseEntity<BonLivraisonEntity> getBonLivraison(@PathVariable Integer id) {
        return ResponseEntity.ok(bonLivraisonService.getBonLivraison(id));
    }

    // ✅ 3️⃣ Récupérer le bon de livraison à partir du bon de sortie
    @GetMapping("/bon-sortie/{idBonSortie}")
    public ResponseEntity<BonLivraisonEntity> getBonLivraisonByBonSortie(@PathVariable Integer idBonSortie) {
        return ResponseEntity.ok(bonLivraisonService.getBonLivraisonByBonSortie(idBonSortie));
    }

    // ✅ 4️⃣ Récupérer tous les bons de livraison d’une commande
    @GetMapping("/commande/{idCommande}")
    public ResponseEntity<List<BonLivraisonEntity>> getBonLivraisonByCommande(@PathVariable Integer idCommande) {
        return ResponseEntity.ok(bonLivraisonService.getBonLivraisonByCommande(idCommande));
    }

    // ✅ 5️⃣ Récupérer tous les bons de livraison
    @GetMapping
    public ResponseEntity<List<BonLivraisonEntity>> getAllBonLivraison() {
        return ResponseEntity.ok(bonLivraisonService.getAllBonLivraison());
    }

    // ✅ 6️⃣ Supprimer un bon de livraison
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBonLivraison(@PathVariable Integer id) {
        bonLivraisonService.deleteBonLivraison(id);
        return ResponseEntity.ok("Bon de livraison supprimé avec succès ✅");
    }
}
