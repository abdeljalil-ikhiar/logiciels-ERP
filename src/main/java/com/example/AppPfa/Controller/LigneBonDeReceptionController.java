package com.example.AppPfa.Controller;
import com.example.AppPfa.DAO.Entity.LigneBonDeReceptionEntities;
import com.example.AppPfa.Service.LigneBonDeReceptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lignereception")
@CrossOrigin(origins = "http://localhost:3000")
public class LigneBonDeReceptionController {

    @Autowired
    private LigneBonDeReceptionService ligneService;

    @PostMapping("/add")
    public ResponseEntity<LigneBonDeReceptionEntities> addLigne(@RequestBody LigneBonDeReceptionEntities ligne) {
        return ResponseEntity.ok(ligneService.addLigne(ligne));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<LigneBonDeReceptionEntities> updateLigne(@PathVariable int id,
                                                                 @RequestBody LigneBonDeReceptionEntities ligne) {
        LigneBonDeReceptionEntities updated = ligneService.updateLigne(id, ligne);
        return (updated == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
    }

    @GetMapping("/all")
    public ResponseEntity<List<LigneBonDeReceptionEntities>> getAllLignes() {
        return ResponseEntity.ok(ligneService.getAllLignes());
    }


    @GetMapping("/bon/{bonId}")
    public ResponseEntity<List<LigneBonDeReceptionEntities>> getLignesByBon(@PathVariable int bonId) {
        return ResponseEntity.ok(ligneService.getLignesByBonId(bonId));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLigne(@PathVariable int id) {
        ligneService.deleteLigne(id);
        return ResponseEntity.noContent().build();
    }
}
