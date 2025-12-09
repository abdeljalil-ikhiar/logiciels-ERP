package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.DevisEntity;
import com.example.AppPfa.Service.DevisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devis")
@CrossOrigin(origins = "http://localhost:3000")
public class DevisController {

    @Autowired
    private DevisService devisService;

    @PostMapping("/add")
    public ResponseEntity<?> addDevis(@RequestBody DevisEntity devisEntity) {
        try {
            DevisEntity saved = devisService.addDevis(devisEntity);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<DevisEntity>> getAllDevis() {
        List<DevisEntity> devisList = devisService.getAllDevis();
        return ResponseEntity.ok(devisList);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDevis(@PathVariable int id, @RequestBody DevisEntity devisEntity) {
        try {
            DevisEntity updated = devisService.updateDevis(id, devisEntity);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDevis(@PathVariable int id) {
        try {
            devisService.deleteDevis(id);
            return ResponseEntity.ok("Devis supprimé avec succès ✅");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}