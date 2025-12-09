package com.example.AppPfa.Repository;
import com.example.AppPfa.DAO.Entity.LigneBonDeReceptionEntities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneBon_de_receptionRepository extends JpaRepository<LigneBonDeReceptionEntities,Integer> {
    List<LigneBonDeReceptionEntities> findByBonDeReceptionEntityId(Integer bonId);
}
