package com.example.AppPfa.Controller;
import com.example.AppPfa.DAO.Entity.BonDeLivraisonFournisseurEntity;
import com.example.AppPfa.Service.BonDeLivraisonFournisseurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bonlivraisonfournisseur")
@CrossOrigin(origins = "http://localhost:3000")
public class BonDeLivraisonFournisseurController {

    @Autowired
    private BonDeLivraisonFournisseurService service;

    // =============================================
    // ✅ ENDPOINTS CRUD
    // =============================================

    /**
     * GET /api/bonlivraisonfournisseur/all
     * Récupérer tous les bons de livraison
     */
    @GetMapping("/all")
    public ResponseEntity<List<BonDeLivraisonFournisseurEntity>> getAll() {
        try {
            System.out.println("📋 GET /all");
            List<BonDeLivraisonFournisseurEntity> bons = service.getAllBonLivraison();
            return ResponseEntity.ok(bons);
        } catch (Exception e) {
            System.err.println("❌ Erreur getAll: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/bonlivraisonfournisseur/{id}
     * Récupérer un bon par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BonDeLivraisonFournisseurEntity> getById(@PathVariable Integer id) {
        try {
            System.out.println("📋 GET /" + id);
            BonDeLivraisonFournisseurEntity bl = service.getBonLivraison(id);
            return ResponseEntity.ok(bl);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/bonlivraisonfournisseur/bonreception/{bonReceptionId}
     * Récupérer par bon de réception
     */
    @GetMapping("/bonreception/{bonReceptionId}")
    public ResponseEntity<BonDeLivraisonFournisseurEntity> getByBonReception(
            @PathVariable Integer bonReceptionId) {
        try {
            BonDeLivraisonFournisseurEntity bl = service.getBonLivraisonByBonReception(bonReceptionId);
            return ResponseEntity.ok(bl);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/bonlivraisonfournisseur/fournisseur/{fournisseurId}
     * Récupérer par fournisseur
     */
    @GetMapping("/fournisseur/{fournisseurId}")
    public ResponseEntity<List<BonDeLivraisonFournisseurEntity>> getByFournisseur(
            @PathVariable Integer fournisseurId) {
        List<BonDeLivraisonFournisseurEntity> bons = service.getBonLivraisonByFournisseur(fournisseurId);
        return ResponseEntity.ok(bons);
    }

    /**
     * POST /api/bonlivraisonfournisseur/generer/{bonReceptionId}
     * Créer un bon de livraison
     */
    @PostMapping("/generer/{bonReceptionId}")
    public ResponseEntity<?> creerBonLivraison(
            @PathVariable Integer bonReceptionId,
            @RequestParam(required = false) String numeroBLFournisseur,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateLivraison,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateBLFournisseur,
            @RequestParam(required = false) String commentaire) {
        try {
            System.out.println("📦 POST /generer/" + bonReceptionId);
            BonDeLivraisonFournisseurEntity bl = service.creerBonLivraison(
                    bonReceptionId, numeroBLFournisseur, dateLivraison, dateBLFournisseur, commentaire
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(bl);
        } catch (RuntimeException e) {
            System.err.println("❌ Erreur création: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/bonlivraisonfournisseur/delete/{id}
     * Supprimer un bon de livraison
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            System.out.println("🗑️ DELETE /delete/" + id);
            service.deleteBonLivraison(id);
            return ResponseEntity.ok(Map.of("message", "Supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // =============================================
    // ✅ ENDPOINTS PDF
    // =============================================

    /**
     * POST /api/bonlivraisonfournisseur/{id}/upload-pdf
     * Upload un fichier PDF
     */
    @PostMapping(value = "/{id}/upload-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPdf(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file) {

        System.out.println("📥 POST /" + id + "/upload-pdf");
        System.out.println("📄 Fichier: " + file.getOriginalFilename() + " (" + file.getSize() + " bytes)");

        try {
            // Validations
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Fichier vide"));
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.equals("application/pdf")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Seuls les fichiers PDF sont acceptés"));
            }

            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("error", "Fichier trop volumineux (max 10MB)"));
            }

            // Upload
            service.uploadPdf(id, file);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "PDF uploadé avec succès");
            response.put("fileName", file.getOriginalFilename());
            response.put("size", file.getSize());

            System.out.println("✅ PDF uploadé avec succès");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.err.println("❌ Erreur runtime: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Erreur upload: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'upload: " + e.getMessage()));
        }
    }

    /**
     * GET /api/bonlivraisonfournisseur/{id}/pdf
     * Télécharger le PDF
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Integer id) {
        try {
            System.out.println("📥 GET /" + id + "/pdf");
            BonDeLivraisonFournisseurEntity bl = service.getBonLivraison(id);

            if (!bl.hasPdf()) {
                return ResponseEntity.notFound().build();
            }

            String fileName = bl.getPdfFileName() != null ? bl.getPdfFileName() : "bon_livraison_" + id + ".pdf";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(bl.getPdfFile());

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/bonlivraisonfournisseur/{id}/view-pdf
     * Voir le PDF dans le navigateur
     */
    @GetMapping("/{id}/view-pdf")
    public ResponseEntity<byte[]> viewPdf(@PathVariable Integer id) {
        try {
            System.out.println("👁️ GET /" + id + "/view-pdf");
            BonDeLivraisonFournisseurEntity bl = service.getBonLivraison(id);

            if (!bl.hasPdf()) {
                return ResponseEntity.notFound().build();
            }

            String fileName = bl.getPdfFileName() != null ? bl.getPdfFileName() : "bon_livraison_" + id + ".pdf";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(bl.getPdfFile());

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/bonlivraisonfournisseur/{id}/has-pdf
     * Vérifier si un PDF existe
     */
    @GetMapping("/{id}/has-pdf")
    public ResponseEntity<Map<String, Object>> hasPdf(@PathVariable Integer id) {
        try {
            BonDeLivraisonFournisseurEntity bl = service.getBonLivraison(id);

            Map<String, Object> response = new HashMap<>();
            response.put("hasPdf", bl.hasPdf());

            if (bl.hasPdf()) {
                response.put("fileName", bl.getPdfFileName());
                response.put("uploadDate", bl.getPdfUploadDate());
            }

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/bonlivraisonfournisseur/{id}/delete-pdf
     * Supprimer le PDF
     */
    @DeleteMapping("/{id}/delete-pdf")
    public ResponseEntity<?> deletePdf(@PathVariable Integer id) {
        try {
            System.out.println("🗑️ DELETE /" + id + "/delete-pdf");
            service.deletePdf(id);
            return ResponseEntity.ok(Map.of("message", "PDF supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // =============================================
    // ✅ ENDPOINT DE TEST
    // =============================================

    /**
     * GET /api/bonlivraisonfournisseur/test
     * Endpoint de test
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("✅ API BonLivraisonFournisseur fonctionne !");
    }
}