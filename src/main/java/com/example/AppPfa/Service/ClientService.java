package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.ClientEntity;
import com.example.AppPfa.Repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ClientService implements ClientManager{
    @Autowired
    private ClientRepository clientRepository;
    @Override
    public ClientEntity addClient(ClientEntity clientEntity) {

        return clientRepository.save(clientEntity);
    }

    @Override
    public ClientEntity updateClient(int id, ClientEntity clientEntity) {
        ClientEntity existing = clientRepository.findById(id).orElse(null);
        if (existing!=null )
            existing.setNomclient(clientEntity.getNomclient());
            existing.setIce(clientEntity.getIce());
            existing.setAdress(clientEntity.getAdress());
            existing.setActivte(clientEntity.getActivte());
            existing.setEmail(clientEntity.getEmail());
            existing.setTelephone(clientEntity.getTelephone());
            existing.setNomcommercial(clientEntity.getNomcommercial());

        return clientRepository.save(existing);
    }

    @Override
    public void deletClient(int id) {
     clientRepository.deleteById(id);
    }

    @Override
    public List<ClientEntity> getAllClient() {
        return clientRepository.findAll();
    }
}
