package com.example.AppPfa.DAO.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "commande_achats")
public class CommandeAchatsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String numerocommandeAchats;
    private LocalDate datecommandeAchats;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fournisseur_id")
    @JsonIgnoreProperties({"commandeAchatsEntities"})
    private FournisseurEntity fournisseurEntity;

    @OneToMany(mappedBy = "commandeAchatsEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"commandeAchatsEntity"})
    private List<LigneCommandeAchatsEntity> listAchats = new ArrayList<>();

    private Double totalHT;
    private Double totalTTC;

    @OneToMany(mappedBy = "commandeAchatsEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<BonDeReceptionEntity> bonDeReceptionEntities = new ArrayList<>();
}