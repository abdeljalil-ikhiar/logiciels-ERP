package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.InventaireEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventaireRepository extends JpaRepository<InventaireEntity,Integer> {

}
