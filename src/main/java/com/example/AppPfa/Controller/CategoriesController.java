package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.CategoriesEntity;
import com.example.AppPfa.Service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:3000")
public class CategoriesController {
    @Autowired
    private CategoriesService categoriesService;
    @GetMapping("/all")
    public List<CategoriesEntity> getAllCategories(){
        return categoriesService.getAllCategories();
    }

    @PostMapping("/add")
    public CategoriesEntity addCategories(@RequestBody CategoriesEntity categoriesEntity){
        return categoriesService.addCategories(categoriesEntity);
    }
    @PutMapping("/update/{id}")
    public CategoriesEntity UpdateCategorie(@PathVariable int id,@RequestBody CategoriesEntity categoriesEntity){
        return categoriesService.updateCategories(id, categoriesEntity);
    }
    @DeleteMapping("/delete/{id}")
    public String deletCategories(@PathVariable int id){
        categoriesService.deleteCategories(id);
        return "categories avec"+id+"supprime avec succes";
    }
}
