package com.example.AppPfa.Controller;
import com.example.AppPfa.DAO.Entity.BonDeReceptionEntity;
import com.example.AppPfa.Service.BonDeReceptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bonreception")
@CrossOrigin(origins = "http://localhost:3000")
public class BonDeReceptionController {

    @Autowired
    private BonDeReceptionService bonDeReceptionService;

    @PostMapping("/add")
    public ResponseEntity<BonDeReceptionEntity> addBonReception(@RequestBody BonDeReceptionEntity bonDeReceptionEntity) {
        BonDeReceptionEntity saved = bonDeReceptionService.addBonReception(bonDeReceptionEntity);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BonDeReceptionEntity> updateBonReception(@PathVariable int id,
                                                                   @RequestBody BonDeReceptionEntity bonReception) {
        BonDeReceptionEntity updated = bonDeReceptionService.updateBonReception(id, bonReception);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BonDeReceptionEntity>> getAllBonReception() {
        List<BonDeReceptionEntity> list = bonDeReceptionService.getAllBonReception();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBonReception(@PathVariable int id) {
        bonDeReceptionService.deleteBonReception(id);
        return ResponseEntity.noContent().build();
    }
}