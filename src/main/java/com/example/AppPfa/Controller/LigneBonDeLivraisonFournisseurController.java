package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.LigneBonDeLivraisonFournisseurEntity;
import com.example.AppPfa.Service.LigneBonDeLivraisonFournisseurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lignebonlivraisonfournisseur")
@CrossOrigin(origins = "http://localhost:3000")
public class LigneBonDeLivraisonFournisseurController {

    @Autowired
    private LigneBonDeLivraisonFournisseurService service;

    // 🔹 GET /api/lignebonlivraisonfournisseur/all
    @GetMapping("/all")
    public ResponseEntity<List<LigneBonDeLivraisonFournisseurEntity>> getAll() {
        try {
            List<LigneBonDeLivraisonFournisseurEntity> lignes = service.getAllLignesBonLivraison();
            return ResponseEntity.ok(lignes);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des lignes BLF: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 🔹 GET /api/lignebonlivraisonfournisseur/{id}
    @GetMapping("/{id}")
    public ResponseEntity<LigneBonDeLivraisonFournisseurEntity> getById(@PathVariable Integer id) {
        try {
            LigneBonDeLivraisonFournisseurEntity ligne = service.getLigneBonLivraisonById(id);
            return ResponseEntity.ok(ligne);
        } catch (RuntimeException e) {
            System.err.println("❌ Ligne BLF non trouvée: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("❌ Erreur serveur (get ligne BLF): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 🔹 GET /api/lignebonlivraisonfournisseur/bonlivraison/{bonLivraisonId}
    @GetMapping("/bonlivraison/{bonLivraisonId}")
    public ResponseEntity<List<LigneBonDeLivraisonFournisseurEntity>> getByBonLivraison(
            @PathVariable Integer bonLivraisonId) {
        try {
            List<LigneBonDeLivraisonFournisseurEntity> lignes =
                    service.getLignesByBonLivraison(bonLivraisonId);
            return ResponseEntity.ok(lignes);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des lignes par BLF: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 🔹 POST /api/lignebonlivraisonfournisseur/add/{bonLivraisonId}/{ligneReceptionId}
    //     Ajoute une ligne à un BLF à partir d'une ligne de BR
    @PostMapping("/add/{bonLivraisonId}/{ligneReceptionId}")
    public ResponseEntity<?> addLigne(
            @PathVariable Integer bonLivraisonId,
            @PathVariable Integer ligneReceptionId) {
        try {
            System.out.println("📥 Ajout ligne BLF (BLF ID: " + bonLivraisonId +
                    ", Ligne BR ID: " + ligneReceptionId + ")");
            LigneBonDeLivraisonFournisseurEntity ligne =
                    service.addLigneBonLivraison(bonLivraisonId, ligneReceptionId);
            return ResponseEntity.status(HttpStatus.CREATED).body(ligne);
        } catch (RuntimeException e) {
            System.err.println("❌ Erreur métier (add ligne BLF): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erreur serveur (add ligne BLF): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur: " + e.getMessage());
        }
    }

    // 🔹 DELETE /api/lignebonlivraisonfournisseur/delete/{id}
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        try {
            service.deleteLigneBonLivraison(id);
            return ResponseEntity.ok("Ligne BL Fournisseur avec ID " + id + " supprimée avec succès.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erreur serveur (delete ligne BLF): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur: " + e.getMessage());
        }
    }
}