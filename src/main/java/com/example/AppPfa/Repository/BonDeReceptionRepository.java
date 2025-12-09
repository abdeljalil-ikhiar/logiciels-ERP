package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.BonDeReceptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface BonDeReceptionRepository extends JpaRepository<BonDeReceptionEntity, Integer> {

    Optional<BonDeReceptionEntity> findByNumeroreception(String numeroreception);

    @Query("SELECT COUNT(b) FROM BonDeReceptionEntity b WHERE b.numeroreception LIKE :prefix || '%'")
    long countByNumeroreceptionStartingWith(@Param("prefix") String prefix);

    List<BonDeReceptionEntity> findByCommandeAchatsEntityId(Integer commandeAchatsId);
}
