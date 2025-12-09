// LigneCommandeEntity.java
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
@Table(name = "lignecommande")
public class LigneCommandeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    @JsonIgnoreProperties({"lignesCommande", "client"})
    @ToString.Exclude
    private CommandeEntity commandeEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produit_id", nullable = false)
    @JsonIgnoreProperties({"ligneCommandeEntities", "ligneDevisEntities", "ligneBonSortieEchange"})
    private ProduitEntity produit;

    @Column(nullable = false)
    private Double quantite;

    @Column(nullable = false)
    private Double prixUnitaire;

    @Column(nullable = false)
    private Double totalHT;

    @Column(nullable = false)
    private Double totalTTC;

    // ✅ CORRECTION : Enlever cascade et orphanRemoval
    @OneToMany(mappedBy = "ligneCommande", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<LigneBonSortieEntity> lignesBonSortie = new ArrayList<>();

    // ✅ CORRECTION : Enlever cascade et orphanRemoval
    @OneToMany(mappedBy = "ligneCommande", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<LigneBonLivraisonEntity> lignesBonLivraison = new ArrayList<>();
}