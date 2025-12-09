package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.CategoriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriesRepository extends JpaRepository<CategoriesEntity, Integer> {
}
