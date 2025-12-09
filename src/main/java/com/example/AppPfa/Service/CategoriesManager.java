package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.CategoriesEntity;
import java.util.List;

public interface CategoriesManager {
    CategoriesEntity addCategories(CategoriesEntity categoriesEntity);
    CategoriesEntity updateCategories(int id, CategoriesEntity categoriesEntity);
    void deleteCategories(int id);
    List<CategoriesEntity> getAllCategories();
    CategoriesEntity getCategoriesById(int id);
}