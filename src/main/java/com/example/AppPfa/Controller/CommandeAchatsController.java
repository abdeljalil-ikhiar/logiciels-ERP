package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.CommandeAchatsEntity;
import com.example.AppPfa.Service.CommandeAchatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandeachats")
@CrossOrigin(origins = "http://localhost:3000")
public class CommandeAchatsController {

    @Autowired
    private CommandeAchatsService commandeAchatsService;

    @GetMapping("/all")
    public ResponseEntity<List<CommandeAchatsEntity>> getAllCommandeAchats() {
        try {
            List<CommandeAchatsEntity> commandes = commandeAchatsService.getCommandeAchats();
            return ResponseEntity.ok(commandes);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des commandes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeAchatsEntity> getCommandeAchatsById(@PathVariable int id) {
        try {
            // Vous devrez ajouter cette méthode dans le service
            CommandeAchatsEntity commande = commandeAchatsService.getCommandeAchats()
                    .stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
            return ResponseEntity.ok(commande);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCommandeAchats(@RequestBody CommandeAchatsEntity commandeAchatsEntity) {
        try {
            System.out.println("📥 Réception de la commande d'achats");
            System.out.println("📦 Fournisseur ID: " +
                    (commandeAchatsEntity.getFournisseurEntity() != null ?
                            commandeAchatsEntity.getFournisseurEntity().getId() : "null"));
            System.out.println("📋 Nombre de lignes: " +
                    (commandeAchatsEntity.getListAchats() != null ?
                            commandeAchatsEntity.getListAchats().size() : 0));

            CommandeAchatsEntity saved = commandeAchatsService.addCommandeAchats(commandeAchatsEntity);

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            System.err.println("❌ Erreur métier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erreur serveur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCommandeAchats(
            @PathVariable int id,
            @RequestBody CommandeAchatsEntity commandeAchatsEntity) {
        try {
            CommandeAchatsEntity updated = commandeAchatsService.updateCommandeAchats(id, commandeAchatsEntity);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCommandeAchats(@PathVariable int id) {
        try {
            commandeAchatsService.deleteCommandeAchats(id);
            return ResponseEntity.ok("CommandeAchats avec ID " + id + " supprimée avec succès.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur: " + e.getMessage());
        }
    }
}