package com.example.AppPfa.Controller;
import com.example.AppPfa.DAO.Entity.LigneCommandeAchatsEntity;
import com.example.AppPfa.Service.LigneCommandeAchatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/lignecommandeachats")
@CrossOrigin(origins = "http://localhost:3000")
public class LigneCommandeAchatsController {

    @Autowired
    private LigneCommandeAchatsService ligneCommandeAchatsService;

    @GetMapping("/all")
    public List<LigneCommandeAchatsEntity> getAllLigneCommandeAchats() {
        return ligneCommandeAchatsService.getLigneCommandeAchats();
    }

    @PostMapping("/add")
    public LigneCommandeAchatsEntity addLigneCommandeAchats(@RequestBody LigneCommandeAchatsEntity ligneCommandeAchatsEntity) {
        return ligneCommandeAchatsService.addLigneCommandeAchats(ligneCommandeAchatsEntity);
    }

    @PutMapping("/update/{id}")
    public LigneCommandeAchatsEntity updateLigneCommandeAchats(@PathVariable int id, @RequestBody LigneCommandeAchatsEntity ligneCommandeAchatsEntity) {
        return ligneCommandeAchatsService.updateLigneCommandeAchats(id, ligneCommandeAchatsEntity);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteLigneCommandeAchats(@PathVariable int id) {
        ligneCommandeAchatsService.deleteLigneCommandeAchats(id);
        return "LigneCommandeAchats avec ID " + id + " supprimé avec succès.";
    }
}