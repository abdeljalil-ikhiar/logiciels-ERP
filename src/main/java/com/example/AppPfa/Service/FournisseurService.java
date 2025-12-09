package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.FournisseurEntity;
import com.example.AppPfa.Repository.FournisseurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class FournisseurService implements FournisseurManager{
    @Autowired
    private FournisseurRepository fournisseurRepository;
    @Override
    public FournisseurEntity addFournniseur(FournisseurEntity fournisseurEntity) {

        return fournisseurRepository.save(fournisseurEntity);
    }

    @Override
    public List<FournisseurEntity> getAllFournisseur() {
        return fournisseurRepository.findAll();
    }

    @Override
    public FournisseurEntity updateFournisseur(int id, FournisseurEntity fournisseurEntity) {
            FournisseurEntity existing = fournisseurRepository.findById(id).orElseThrow(()->new RuntimeException());
            if (existing!= null)
            existing.setNomfournisseur(fournisseurEntity.getNomfournisseur());
            existing.setIcefournisseur(fournisseurEntity.getIcefournisseur());
            existing.setTelephone(fournisseurEntity.getTelephone());
            existing.setAdressfournisseur(fournisseurEntity.getAdressfournisseur());
            existing.setNomachteur(fournisseurEntity.getNomachteur());
            existing.setActviteFournisseur(fournisseurEntity.getActviteFournisseur());
            existing.setEmail(fournisseurEntity.getEmail());
            return fournisseurRepository.save(existing);
    }

    @Override
    public void deleteFournisseur(int id) {
       fournisseurRepository.deleteById(id);
    }
}
