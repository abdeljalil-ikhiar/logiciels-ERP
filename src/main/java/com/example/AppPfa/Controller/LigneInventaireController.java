package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.LigneInventaireEntity;
import com.example.AppPfa.Service.LigneInventaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ligneInventaire")
@CrossOrigin(origins = "http://localhost:3000")
public class LigneInventaireController {

    @Autowired
    private LigneInventaireService ligneInventaireService;


    @GetMapping("/all")
    public List<LigneInventaireEntity> getAll() {
        return ligneInventaireService.getAllLigneInventaire();
    }


    @PostMapping("/add")
    public LigneInventaireEntity add(@RequestBody LigneInventaireEntity ligneInventaireEntity) {
        return ligneInventaireService.addLigneInventaire(ligneInventaireEntity);
    }


    @PutMapping("/{id}/update")
    public LigneInventaireEntity update(@PathVariable Integer id, @RequestBody LigneInventaireEntity ligneInventaireEntity) {
        return ligneInventaireService.updateLigneInventaire(id, ligneInventaireEntity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        ligneInventaireService.deleteLigneInventaire(id);
    }
}
