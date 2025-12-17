package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.LigneFactureFournisseurEntity;
import com.example.AppPfa.Service.LigneFactureFournisseurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lignefacturefournisseur")
@CrossOrigin(origins = "http://localhost:3000")
public class LigneFactureFournisseurController {

    @Autowired
    private LigneFactureFournisseurService ligneFactureFournisseurService;

    @PostMapping("/add")
    public ResponseEntity<LigneFactureFournisseurEntity> addLigne(
            @RequestParam Integer factureId,
            @RequestParam Integer ligneBonLivraisonFournisseurId) {
        return ResponseEntity.ok(
                ligneFactureFournisseurService.addLigneFactureFournisseur(factureId, ligneBonLivraisonFournisseurId)
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<LigneFactureFournisseurEntity>> getAllLignes() {
        return ResponseEntity.ok(ligneFactureFournisseurService.getAllLignesFactureFournisseur());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LigneFactureFournisseurEntity> getLigneById(@PathVariable int id) {
        return ResponseEntity.ok(ligneFactureFournisseurService.getLigneFactureFournisseurById(id));
    }

    @GetMapping("/facture/{factureId}")
    public ResponseEntity<List<LigneFactureFournisseurEntity>> getLignesByFacture(@PathVariable int factureId) {
        return ResponseEntity.ok(ligneFactureFournisseurService.getLignesByFactureFournisseurId(factureId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLigne(@PathVariable int id) {
        ligneFactureFournisseurService.deleteLigneFactureFournisseur(id);
        return ResponseEntity.noContent().build();
    }
}