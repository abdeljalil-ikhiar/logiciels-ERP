package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.DepotEntity;
import com.example.AppPfa.Service.DepotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/depot")
@CrossOrigin(origins = "http://localhost:3000")
public class DepotController {
    @Autowired
    private DepotService depotService;
    @GetMapping("/all")
    public List<DepotEntity> getAllDepto(){
        return depotService.getAllDepot();
    }
    @PostMapping("/add")
    public DepotEntity addDepot(@RequestBody DepotEntity depotEntity){
        return depotService.AddDepot(depotEntity);
    }
    @PutMapping("/update/{id}")
    public DepotEntity updateDepot(@PathVariable int id,@RequestBody DepotEntity depotEntity){
        return depotService.UpdateDepot(id, depotEntity);
    }
    @DeleteMapping("/delete/{id}")
    public String deleteDepot(@PathVariable int id){
        depotService.DeletDepot(id);
     return "Depot avec ID " + id + " supprimé avec succès.";
    }
}
