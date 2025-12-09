package com.example.AppPfa.Service;
import com.example.AppPfa.DAO.Entity.LigneCommandeEntity;
import com.example.AppPfa.Repository.LigneCommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LigneCommandeService implements LigneCommandeManager{
    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;
    @Override
    public LigneCommandeEntity addLigneCommande(LigneCommandeEntity ligneCommandeEntity) {
        calculerTotalLigne(ligneCommandeEntity);
        return ligneCommandeRepository.save(ligneCommandeEntity);


    }

    @Override
    public LigneCommandeEntity updateLigneCommande(int id, LigneCommandeEntity ligneCommandeEntity) {
        LigneCommandeEntity existing = ligneCommandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LigneCommande non trouvée avec id : " + id));

            existing.setQuantite(ligneCommandeEntity.getQuantite());
            existing.setPrixUnitaire(ligneCommandeEntity.getPrixUnitaire());
            existing.setProduit(ligneCommandeEntity.getProduit());
            // recalcul automatique
        calculerTotalLigne(existing);
            return ligneCommandeRepository.save(existing);



    }

    @Override
    public List<LigneCommandeEntity> getLigneCommande() {
        return ligneCommandeRepository.findAll();
    }

    @Override
    public void deleteLigneCommande(int id) {
        ligneCommandeRepository.deleteById(id);

    }

    @Override
    public void calculerTotalLigne(LigneCommandeEntity ligneCommandeEntity) {
        if (ligneCommandeEntity.getProduit()!=null){
            double totalht=ligneCommandeEntity.getQuantite()*ligneCommandeEntity.getPrixUnitaire();
            double totalttc= totalht * (1 + ligneCommandeEntity.getProduit().getTva()/100);
            ligneCommandeEntity.setTotalHT(totalht);
            ligneCommandeEntity.setTotalTTC(totalttc);
        }

    }
}
