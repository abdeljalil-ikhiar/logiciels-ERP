package com.example.AppPfa.Controller;
import com.example.AppPfa.DAO.Entity.FournisseurEntity;
import com.example.AppPfa.Service.FournisseurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/fournisseur")
@CrossOrigin(origins = "http://localhost:3000")
public class FounisseurController {  // ✅ Fixed spelling
    @Autowired private FournisseurService fournisseurService;

    @GetMapping("/all")
    public List<FournisseurEntity> getfournisseur(){
        return fournisseurService.getAllFournisseur();
    }

    @PostMapping("/add")
    public FournisseurEntity addfournisseur(@RequestBody FournisseurEntity fournisseurEntity){
        return fournisseurService.addFournniseur(fournisseurEntity);
    }

    @PutMapping("/update/{id}")
    public FournisseurEntity updatFournisseur(@PathVariable int id, @RequestBody FournisseurEntity fournisseurEntity){
        return fournisseurService.updateFournisseur(id, fournisseurEntity);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteFournisseur(@PathVariable int id){  // ✅ Return string, not null
        fournisseurService.deleteFournisseur(id);
        return "Fournisseur supprimé avec succès";
    }
}
