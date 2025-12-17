package com.example.AppPfa.DAO.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ligne_commande_achats")
public class LigneCommandeAchatsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produit_id", nullable = false)
    private ProduitEntity produit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_achats_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private CommandeAchatsEntity commandeAchatsEntity;

    @Column(nullable = false)
    private Double quantite;

    @Column(nullable = false)
    private Double prixUnitaire;

    @Column(nullable = false)
    private Double totalHT = 0.0;

    @Column(nullable = false)
    private Double totalTTC = 0.0;

    @OneToMany(mappedBy = "ligneCommandeAchatsEntity", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<LigneBonDeReceptionEntities> ligneBonDeReceptionEntities = new ArrayList<>();
    @OneToMany(mappedBy = "ligneCommandeAchatsEntity", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<LigneBonDeLivraisonFournisseurEntity> ligneBonDeLivraisonFournisseurEntities = new ArrayList<>();
}