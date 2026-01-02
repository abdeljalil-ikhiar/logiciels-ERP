package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.ProduitEntity;
import com.example.AppPfa.Service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produit")
@CrossOrigin(origins = "http://localhost:3000")
public class ProduitController {
    @Autowired
    private ProduitService produitService;
    @GetMapping("/all")
    public List<ProduitEntity> getAllProduit(){
        return produitService.getAllProduit();
    }
    @PostMapping("/add")
    public ProduitEntity addProduit(@RequestBody ProduitEntity produitEntity){
        return produitService.addProduit(produitEntity);
        }
    @PutMapping("/update/{id}")
    public ProduitEntity UpdateProduit(@PathVariable int id ,@RequestBody ProduitEntity produitEntity){
        return produitService.updateProduit(id, produitEntity);
    }
    @DeleteMapping("/delete/{id}")
    public String deleteProduit(@PathVariable int id){
        produitService.deleteProduit(id);
        return "Produit avec ID " + id + " supprimé avec succès.";
    }
}
