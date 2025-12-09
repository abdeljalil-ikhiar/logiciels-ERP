package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.LigneBonSortieEntity;
import com.example.AppPfa.Service.LigneBonSortieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/lignebonsortie")
@CrossOrigin(origins = "http://localhost:3000")
public class LigneBonSortieController {
    @Autowired
    private LigneBonSortieService ligneBonSortieService;

    @PostMapping("/add")
    public ResponseEntity<LigneBonSortieEntity> addLigneBonSortie(@RequestBody LigneBonSortieEntity ligneBonSortie) {
        LigneBonSortieEntity saved = ligneBonSortieService.addLigneBonSortie(ligneBonSortie);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<LigneBonSortieEntity> updateLigneBonSortie(@PathVariable int id, @RequestBody LigneBonSortieEntity ligneBonSortie) {
        try {
            LigneBonSortieEntity updated = ligneBonSortieService.updateLigneBonSortie(id, ligneBonSortie);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<LigneBonSortieEntity>> getAllLigneBonSortie() {
        List<LigneBonSortieEntity> list = ligneBonSortieService.getAllLigneBonSortie();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLigneBonSortie(@PathVariable int id) {
        try {
            ligneBonSortieService.deleteLigneBonSortie(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
