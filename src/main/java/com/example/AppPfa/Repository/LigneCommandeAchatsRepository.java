package com.example.AppPfa.Repository;
import com.example.AppPfa.DAO.Entity.LigneCommandeAchatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneCommandeAchatsRepository extends JpaRepository<LigneCommandeAchatsEntity, Integer> {
    List<LigneCommandeAchatsEntity> findByCommandeAchatsEntityId(Integer commandeAchatsId);
    List<LigneCommandeAchatsEntity> findByProduitId(Integer produitId);
    void deleteByCommandeAchatsEntityId(Integer commandeAchatsId);
}