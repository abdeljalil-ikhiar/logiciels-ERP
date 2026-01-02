package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.LigneAvoirEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneAvoirRepository extends JpaRepository<LigneAvoirEntity, Integer> {

    // ✅ Lignes par avoir
    List<LigneAvoirEntity> findByAvoirId(Integer avoirId);

    // ✅ Lignes par produit
    List<LigneAvoirEntity> findByProduitId(Integer produitId);

    // ✅ Lignes par ligne retour
    List<LigneAvoirEntity> findByLigneRetourId(Integer ligneRetourId);

    // ✅ Somme total HT par avoir
    @Query("SELECT COALESCE(SUM(l.totalHT), 0) FROM LigneAvoirEntity l WHERE l.avoir.id = :avoirId")
    Double sumTotalHTByAvoir(@Param("avoirId") Integer avoirId);
}