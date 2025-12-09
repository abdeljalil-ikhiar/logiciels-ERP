package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.BonDeReceptionEntity;
import com.example.AppPfa.Service.BonDeReceptionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bonreception")
@CrossOrigin(origins = "http://localhost:3000")
public class BonDeReceptionController {

    @Autowired
    private BonDeReceptionManager bonDeReceptionManager;

    @PostMapping("/add")
    public ResponseEntity<BonDeReceptionEntity> addBonReception(@RequestBody BonDeReceptionEntity bonDeReceptionEntity) {
        BonDeReceptionEntity saved = bonDeReceptionManager.addBonReception(bonDeReceptionEntity);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BonDeReceptionEntity> updateBonReception(@PathVariable int id,
                                                                   @RequestBody BonDeReceptionEntity bonDeReceptionEntity) {
        BonDeReceptionEntity updated = bonDeReceptionManager.updateBonReception(id, bonDeReceptionEntity);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BonDeReceptionEntity>> getAllBonReception() {
        return ResponseEntity.ok(bonDeReceptionManager.getAllBonReception());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BonDeReceptionEntity> getBonReceptionById(@PathVariable int id) {
        BonDeReceptionEntity bon = bonDeReceptionManager.getBonReceptionById(id);
        return (bon == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(bon);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBonReception(@PathVariable int id) {
        bonDeReceptionManager.deleteBonReception(id);
        return ResponseEntity.noContent().build();
    }
}
