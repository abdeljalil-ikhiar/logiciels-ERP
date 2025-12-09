package com.example.AppPfa.Service;
import com.example.AppPfa.DAO.Entity.CategoriesEntity;
import com.example.AppPfa.Repository.CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriesService implements CategoriesManager {

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Override
    public CategoriesEntity addCategories(CategoriesEntity categoriesEntity) {
        return categoriesRepository.save(categoriesEntity);
    }

    @Override
    public CategoriesEntity updateCategories(int id, CategoriesEntity categoriesEntity) {
        CategoriesEntity existing = categoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        existing.setNomCategories(categoriesEntity.getNomCategories());
        return categoriesRepository.save(existing);
    }

    @Override
    public void deleteCategories(int id) {
        if (!categoriesRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoriesRepository.deleteById(id);
    }

    @Override
    public List<CategoriesEntity> getAllCategories() {
        return categoriesRepository.findAll();
    }

    @Override
    public CategoriesEntity getCategoriesById(int id) {
        return categoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }
}