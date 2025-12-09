package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.DepotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepotRepository extends JpaRepository<DepotEntity,Integer> {
    boolean existsByNomdepot(String nomdepot);
}
