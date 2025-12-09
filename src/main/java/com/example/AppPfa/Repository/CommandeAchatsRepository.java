package com.example.AppPfa.Repository;

import com.example.AppPfa.DAO.Entity.CommandeAchatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandeAchatsRepository extends JpaRepository<CommandeAchatsEntity,Integer> {

}
