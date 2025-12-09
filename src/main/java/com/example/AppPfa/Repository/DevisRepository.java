package com.example.AppPfa.Repository;
import com.example.AppPfa.DAO.Entity.DevisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DevisRepository extends JpaRepository<DevisEntity,Integer> {
    @Query("SELECT COUNT(d) FROM DevisEntity d WHERE FUNCTION('YEAR', d.dateDevis) = :year")
    long countByYear(@Param("year") int year);
}
