package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.BonSortieEntity;
import com.example.AppPfa.DAO.Entity.LigneBonSortieEntity;
import com.example.AppPfa.Service.BonSortieService;
import com.example.AppPfa.Service.LigneBonSortieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bonsortie")
@CrossOrigin(origins = "http://localhost:3000")
public class BonSortieController {

    @Autowired
    private BonSortieService bonSortieService;
    @PostMapping("/add")
    public ResponseEntity<BonSortieEntity> addBonSortie(@RequestBody BonSortieEntity bonSortieEntity) {
        BonSortieEntity saved = bonSortieService.addBonSortie(bonSortieEntity);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BonSortieEntity> updateBonSortie(@PathVariable int id, @RequestBody BonSortieEntity bonSortie) {
        BonSortieEntity updated = bonSortieService.updateBonSortie(id, bonSortie);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BonSortieEntity>> getAllBonSortie() {
        List<BonSortieEntity> list = bonSortieService.getBonSortie();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBonSortie(@PathVariable int id) {
        bonSortieService.deleteBonSortie(id);
        return ResponseEntity.noContent().build();
    }


}
