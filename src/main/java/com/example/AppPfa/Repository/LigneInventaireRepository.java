package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.LigneInventaireEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneInventaireRepository extends JpaRepository<LigneInventaireEntity, Integer> {

    List<LigneInventaireEntity> findByInventaireId(Integer inventaireId);
}