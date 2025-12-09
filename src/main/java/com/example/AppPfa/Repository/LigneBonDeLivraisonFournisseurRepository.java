package com.example.AppPfa.Repository;
import com.example.AppPfa.DAO.Entity.LigneBonDeLivraisonFournisseurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneBonDeLivraisonFournisseurRepository extends JpaRepository<LigneBonDeLivraisonFournisseurEntity, Integer> {

    List<LigneBonDeLivraisonFournisseurEntity> findByBonDeLivraisonFournisseurEntityId(Integer bonLivraisonId);

    void deleteByBonDeLivraisonFournisseurEntityId(Integer bonLivraisonId);
}