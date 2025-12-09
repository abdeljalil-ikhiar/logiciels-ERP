// LigneBonSortieEntity.java
package com.example.AppPfa.DAO.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ligne_bon_sortie")
public class LigneBonSortieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ligne_commande_id", nullable = false)
    @JsonIgnoreProperties({"lignesBonSortie", "lignesBonLivraison", "commandeEntity"})
    private LigneCommandeEntity ligneCommande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bon_sortie_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private BonSortieEntity bonSortie;

    private Double quantiteSortie;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produit_echange_id")
    private ProduitEntity produitEchange;

    private String raisonEchange;

    // ✅ CORRECTION : Enlever cascade et orphanRemoval
    @OneToMany(mappedBy = "ligneBonSortie", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<LigneBonLivraisonEntity> lignesBonLivraison = new ArrayList<>();

}