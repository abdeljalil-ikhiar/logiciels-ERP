package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.FactureFournisseurEntity;
import com.example.AppPfa.Service.FactureFournisseurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facturefournisseur")
@CrossOrigin(origins = "http://localhost:3000")
public class FactureFournisseurController {

    @Autowired
    private FactureFournisseurService factureFournisseurService;

    // ✅ AJOUTER CET ENDPOINT - Récupérer une facture par ID
    @GetMapping("/{id}")
    public ResponseEntity<FactureFournisseurEntity> getFactureFournisseurById(@PathVariable Integer id) {
        try {
            FactureFournisseurEntity facture = factureFournisseurService.getFactureFournisseurById(id);
            return ResponseEntity.ok(facture);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<FactureFournisseurEntity> addFactureFournisseur(
            @RequestBody FactureFournisseurEntity factureFournisseurEntity) {
        return ResponseEntity.ok(factureFournisseurService.addFactureFournisseur(factureFournisseurEntity));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<FactureFournisseurEntity> updateFactureFournisseur(
            @PathVariable int id,
            @RequestBody FactureFournisseurEntity factureFournisseurEntity) {
        FactureFournisseurEntity updated = factureFournisseurService.updateFactureFournisseur(id, factureFournisseurEntity);
        return (updated == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
    }

    @GetMapping("/all")
    public ResponseEntity<List<FactureFournisseurEntity>> getAllFacturesFournisseur() {
        return ResponseEntity.ok(factureFournisseurService.getAllFactureFournisseur());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFactureFournisseur(@PathVariable int id) {
        factureFournisseurService.deleteFactureFournisseur(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/recalculer/{id}")
    public ResponseEntity<FactureFournisseurEntity> recalculerFacture(@PathVariable int id) {
        FactureFournisseurEntity recalculated = factureFournisseurService.recalculerFactureFournisseur(id);
        return (recalculated == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(recalculated);
    }

    @PostMapping("/recalculer-par-bl/{bonLivraisonId}")
    public ResponseEntity<FactureFournisseurEntity> recalculerFactureParBonLivraison(
            @PathVariable int bonLivraisonId) {
        FactureFournisseurEntity recalculated = factureFournisseurService.recalculerFactureFournisseurParBonLivraison(bonLivraisonId);
        return (recalculated == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(recalculated);
    }
    // ✅ AJOUTER CES ENDPOINTS DANS FactureFournisseurController.java

    @PostMapping("/{id}/pdf/upload")
    public ResponseEntity<?> uploadPdf(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        try {
            FactureFournisseurEntity facture = factureFournisseurService.uploadPdf(id, file);
            return ResponseEntity.ok(Map.of(
                    "message", "PDF uploadé avec succès",
                    "fileName", facture.getPdfFileName()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/pdf/download")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Integer id) {
        FactureFournisseurEntity facture = factureFournisseurService.getFactureFournisseurById(id);

        if (facture.getPdfFile() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + facture.getPdfFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(facture.getPdfFile());
    }

    @GetMapping("/{id}/pdf/view")
    public ResponseEntity<byte[]> viewPdf(@PathVariable Integer id) {
        FactureFournisseurEntity facture = factureFournisseurService.getFactureFournisseurById(id);

        if (facture.getPdfFile() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + facture.getPdfFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(facture.getPdfFile());
    }

    @DeleteMapping("/{id}/pdf/delete")
    public ResponseEntity<?> deletePdf(@PathVariable Integer id) {
        try {
            factureFournisseurService.deletePdf(id);
            return ResponseEntity.ok(Map.of("message", "PDF supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}