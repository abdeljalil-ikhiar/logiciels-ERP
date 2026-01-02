package com.example.AppPfa.Controller;

import com.example.AppPfa.DAO.Entity.ClientEntity;
import com.example.AppPfa.Service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "http://localhost:3000")
public class ClientController {

    @Autowired
    private ClientService clientService;

    // Récupérer tous les clients
    @GetMapping("/all")
    public List<ClientEntity> getClients() {
        return clientService.getAllClient();
    }

    // Ajouter un client
    @PostMapping("/add")
    public ClientEntity addClient(@RequestBody ClientEntity clientEntity) {
        return clientService.addClient(clientEntity);
    }

    //  Mettre à jour un client
    @PutMapping("/update/{id}")
    public ClientEntity updateClient(@PathVariable int id, @RequestBody ClientEntity clientEntity) {
        return clientService.updateClient(id, clientEntity);
    }

    //  Supprimer un client
    @DeleteMapping("/delete/{id}")
    public String deleteClient(@PathVariable int id) {
        clientService.deletClient(id);
        return "Client avec ID " + id + " supprimé avec succès.";
    }
}
