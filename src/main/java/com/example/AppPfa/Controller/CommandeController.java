package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.CommandeEntity;
import com.example.AppPfa.Service.CommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commande")
@CrossOrigin(origins = "http://localhost:3000")
public class CommandeController {

    @Autowired
    private CommandeService commandeService;

    @PostMapping("/add")
    public ResponseEntity<?> addCommande(@RequestBody CommandeEntity commandeEntity) {
        try {
            CommandeEntity saved = commandeService.addCommande(commandeEntity);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<CommandeEntity>> getAllCommandes() {
        List<CommandeEntity> commandes = commandeService.getCommande();
        return ResponseEntity.ok(commandes);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCommande(@PathVariable int id, @RequestBody CommandeEntity commandeEntity) {
        try {
            CommandeEntity updated = commandeService.updateCommande(id, commandeEntity);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCommande(@PathVariable int id) {
        try {
            commandeService.deleteCommande(id);
            return ResponseEntity.ok("Commande supprimée avec succès ✅");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}
