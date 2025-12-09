package com.example.AppPfa.DAO.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ligne_bon_de_reception")
public class LigneBonDeReceptionEntities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bon_de_reception_id")
    @JsonIgnore
    @ToString.Exclude
    private BonDeReceptionEntity bonDeReceptionEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ligne_commande_achats_id")
    @JsonIgnoreProperties({"commandeAchatsEntity", "ligneBonDeReceptionEntities"})
    private LigneCommandeAchatsEntity ligneCommandeAchatsEntity;

    private String qtereception;
}