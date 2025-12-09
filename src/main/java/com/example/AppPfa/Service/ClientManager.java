package com.example.AppPfa.Service;

import com.example.AppPfa.DAO.Entity.ClientEntity;

import java.util.List;

public interface ClientManager {
    ClientEntity addClient(ClientEntity clientEntity);
    ClientEntity updateClient(int id, ClientEntity clientEntity);
    void deletClient(int id);
   List<ClientEntity> getAllClient();


}
