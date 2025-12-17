package com.example.AppPfa.DAO.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "produit")
public class ProduitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "produit_id")
    private Integer id;

    @Column(name = "reference", nullable = false, unique = true)
    private String references;

    private String designation;

    @Column(nullable = false)
    private Float tva;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    @ToString.Exclude
    @JsonIgnoreProperties("produits")
    private CategoriesEntity categorie;

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private List<LigneDevisEntity> ligneDevisEntities;

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private List<LigneCommandeEntity> ligneCommandeEntities;

    @OneToMany(mappedBy = "produitEchange", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private List<LigneBonSortieEntity> ligneBonSortieEchange;
    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private List<LigneCommandeAchatsEntity> ligneCommandeAchatsEntities;
    @OneToMany(
            mappedBy = "produit",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @ToString.Exclude
    @JsonIgnore
    private List<LigneInventaireEntity> ligneInventaireEntities;

}