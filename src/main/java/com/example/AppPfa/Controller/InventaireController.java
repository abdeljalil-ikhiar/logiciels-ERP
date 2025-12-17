package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.InventaireEntity;
import com.example.AppPfa.Service.InventaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventaire")
@CrossOrigin(origins = "http://localhost:3000")
public class InventaireController {

    @Autowired
    private InventaireService inventaireService;


    @GetMapping("/all")
    public List<InventaireEntity> getAll() {
        return inventaireService.getAllInventaires();
    }


    @PostMapping("/add")
    public InventaireEntity addInventaire(@RequestBody InventaireEntity inventaireEntity) {
        return inventaireService.addInventaire(inventaireEntity);
    }

    @PutMapping("/{id}/update")
    public InventaireEntity updateInventaire(@PathVariable Integer id, @RequestBody InventaireEntity inventaireEntity) {
        return inventaireService.updateInventaire(id, inventaireEntity);
    }


    @DeleteMapping("/{id}")
    public void deleteInventaire(@PathVariable Integer id) {
        inventaireService.deleteInventaire(id);
    }
}
