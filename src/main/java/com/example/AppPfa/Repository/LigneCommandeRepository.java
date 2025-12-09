package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.LigneCommandeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LigneCommandeRepository extends JpaRepository<LigneCommandeEntity,Integer> {
}
