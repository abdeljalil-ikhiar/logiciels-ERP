package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.ProduitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<ProduitEntity, Integer> {
    List<ProduitEntity> findByCategorie_Id(int categorieId);
}
