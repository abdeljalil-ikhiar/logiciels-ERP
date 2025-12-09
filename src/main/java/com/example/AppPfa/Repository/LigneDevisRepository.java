package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.LigneDevisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LigneDevisRepository extends JpaRepository<LigneDevisEntity,Integer> {
}
