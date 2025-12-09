package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.LigneCommandeEntity;
import com.example.AppPfa.DAO.Entity.LigneDevisEntity;
import com.example.AppPfa.Service.LigneCommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lignecommande")
@CrossOrigin(origins = "http://localhost:3000")

public class LigneCommandeController {
    @Autowired
    private LigneCommandeService ligneCommandeService;
    @GetMapping("/all")
    public List<LigneCommandeEntity> getAllLigneCommande(){
        return ligneCommandeService.getLigneCommande();
    }
    @PostMapping("/add")
    public LigneCommandeEntity addLigneCommande(@RequestBody LigneCommandeEntity ligneCommandeEntity){
        return ligneCommandeService.addLigneCommande(ligneCommandeEntity);
    }
    @PutMapping("/update/{id}")
    public LigneCommandeEntity updateLigneCommande(@PathVariable int id ,@RequestBody LigneCommandeEntity ligneCommandeEntity){
        return ligneCommandeService.updateLigneCommande(id, ligneCommandeEntity);
    }
    @DeleteMapping("/delete/{id}")
    public String deleteLigneCommande(@PathVariable int id){
        ligneCommandeService.deleteLigneCommande(id);
        return "LigneCommande avec ID " + id + " supprimé avec succès.";
    }

}
