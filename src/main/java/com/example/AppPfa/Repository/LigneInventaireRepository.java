package com.example.AppPfa.Repository;
import com.example.AppPfa.DAO.Entity.LigneInventaireEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface LigneInventaireRepository extends JpaRepository<LigneInventaireEntity, Integer> {

    List<LigneInventaireEntity> findByInventaireId(Integer inventaireId);

    @Query("SELECT COALESCE(SUM(l.qteinventaire), 0) FROM LigneInventaireEntity l " +
            "WHERE l.produit.id = :produitId")
    Double sumInventaireByProduit(@Param("produitId") Integer produitId);

}