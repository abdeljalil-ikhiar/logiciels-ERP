package com.example.AppPfa.DAO.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ligne_avoir")
public class LigneAvoirEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avoir_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private AvoirEntity avoir;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produit_id", nullable = false)
    @JsonIgnoreProperties({"ligneDevisEntities", "ligneCommandeEntities", "mouvementsStock"})
    private ProduitEntity produit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ligne_retour_id")
    @JsonIgnoreProperties({"retourProduit"})
    @ToString.Exclude
    private LigneRetourEntity ligneRetour;

    @Column(nullable = false)
    private Double quantite;

    @Column(nullable = false)
    private Double prixUnitaire;

    @Column(nullable = false)
    @Builder.Default
    private Double totalHT = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double totalTVA = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double totalTTC = 0.0;

    @Column(length = 255)
    private String description;
}