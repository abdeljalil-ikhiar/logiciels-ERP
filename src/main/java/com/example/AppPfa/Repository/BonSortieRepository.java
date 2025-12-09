package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.BonSortieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BonSortieRepository extends JpaRepository<BonSortieEntity, Integer> {

    // Spring Data JPA génère automatiquement la requête
    long countByNumeroBonSortieStartingWith(String prefix);
}