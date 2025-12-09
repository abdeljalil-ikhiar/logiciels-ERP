package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.DevisEntity;

import java.util.List;

public interface DevisManager {
        DevisEntity addDevis(DevisEntity devisEntity);
        DevisEntity updateDevis(int id,DevisEntity devisEntity);
        List<DevisEntity> getAllDevis();
        void deleteDevis(int id);
        void calculerTotauxDevis(DevisEntity devisEntity);
}
