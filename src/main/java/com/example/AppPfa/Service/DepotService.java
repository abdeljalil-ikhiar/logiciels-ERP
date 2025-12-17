package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.DepotEntity;
import com.example.AppPfa.Repository.DepotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepotService implements DepotManager {

    @Autowired
    private DepotRepository depotRepository;

    @Override
    public DepotEntity AddDepot(DepotEntity depotEntity) {

        if (depotRepository.existsByNomdepot(depotEntity.getNomdepot())) {
            throw new RuntimeException();
        }
        return depotRepository.save(depotEntity);
    }

    @Override
    public DepotEntity UpdateDepot(int id, DepotEntity depotEntity) {
        DepotEntity existing = depotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(" Dépôt introuvable avec l'id: " + id));

        if (!existing.getNomdepot().equals(depotEntity.getNomdepot()) &&
                depotRepository.existsByNomdepot(depotEntity.getNomdepot())) {
            throw new RuntimeException(" Le dépôt '" + depotEntity.getNomdepot() + "' existe déjà!");
        }

        existing.setNomdepot(depotEntity.getNomdepot());
        existing.setAdress(depotEntity.getAdress());

        return depotRepository.save(existing);
    }

    @Override
    public void DeletDepot(int id) {

        if (!depotRepository.existsById(id)) {
            throw new RuntimeException(" Dépôt introuvable avec l'id: " + id);
        }
        depotRepository.deleteById(id);
    }

    @Override
    public List<DepotEntity> getAllDepot() { // ✅ حذفت parameter لي مافيهش معنى
        return depotRepository.findAll();
    }


    public DepotEntity getDepotById(int id) {
        return depotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(" Dépôt introuvable avec l'id: " + id));
    }
}