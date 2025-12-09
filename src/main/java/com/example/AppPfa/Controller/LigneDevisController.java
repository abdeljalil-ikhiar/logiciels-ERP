package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.LigneDevisEntity;
import com.example.AppPfa.Service.LigneDevisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lignedevis")
@CrossOrigin(origins = "http://localhost:3000")
public class LigneDevisController {
    @Autowired
    private LigneDevisService ligneDevisService;
    @PostMapping("/add")
    public LigneDevisEntity adddevis(@RequestBody LigneDevisEntity ligneDevisEntity){

        return ligneDevisService.addLigneDevis(ligneDevisEntity);

    }
    @GetMapping("/all")
    public List<LigneDevisEntity> getLignedevis(){
        return ligneDevisService.getAllLigneDevis();
    }
    @PutMapping("/update/{id}")
    public LigneDevisEntity updateLignedevis(@PathVariable int id,@RequestBody LigneDevisEntity ligneDevisEntity){
        return ligneDevisService.updateLigneDevis(id, ligneDevisEntity);
    }
    @DeleteMapping("/delete/{id}")
    public String deletedevis(@PathVariable int id){
        ligneDevisService.deleteLigneDevis(id);
        return "Ligne devis supprimée avec succès";
    }
}
