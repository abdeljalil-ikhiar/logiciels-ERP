package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.CommandeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandeRepository extends JpaRepository<CommandeEntity,Integer> {
    @Query("SELECT COUNT(c) FROM CommandeEntity c WHERE YEAR(c.datecommande) = :year")
    long countByYear(@Param("year") int year);


}
