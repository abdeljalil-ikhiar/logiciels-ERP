// BonLivraisonEntity.java
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
@Table(name = "bon_livraison")
@Builder
public class BonLivraisonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String numeroBonLivraison;

    @Column(nullable = false)
    private LocalDate dateLivraison;

    // ✅ Owning side - pas de cascade vers BonSortie
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bon_sortie_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"bonLivraison", "ligneBonSortieEntities"})
    private BonSortieEntity bonSortie;

    // ✅ Cascade ALL + orphanRemoval pour supprimer les lignes automatiquement
    @OneToMany(
            mappedBy = "bonLivraison",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties({"bonLivraison"})
    @ToString.Exclude
    private List<LigneBonLivraisonEntity> lignesBonLivraison = new ArrayList<>();

    @OneToOne(mappedBy = "bonLivraison", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"bonLivraison", "lignesFacture"})
    @ToString.Exclude
    private FactureEntity facture;

    @Column(nullable = false)
    private Double totalHT = 0.0;

    @Column(nullable = false)
    private Double totalTVA = 0.0;

    @Column(nullable = false)
    private Double totalTTC = 0.0;


    @OneToMany(mappedBy = "bonLivraison", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<RetourProduitEntity> retoursProduit;
}