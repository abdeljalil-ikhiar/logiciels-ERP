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
@Table(name = "ligne_retour")
public class LigneRetourEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retour_produit_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private RetourProduitEntity retourProduit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produit_id", nullable = false)
    @JsonIgnoreProperties({"lignesRetour", "mouvementsStock", "etatduStocks"})
    private ProduitEntity produit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produit_echange_id")
    @JsonIgnoreProperties({"lignesRetour", "mouvementsStock", "etatduStocks"})
    private ProduitEntity produitEchange;

    @Column(nullable = false)
    private Double quantiteRetournee;

    @Column(nullable = false)
    @Builder.Default
    private Double prixUnitaire = 0.0;

    // ✅ AJOUTÉ - Prix unitaire HT
    @Column(name = "prix_unitaire_ht", nullable = false)
    @Builder.Default
    private Double prixUnitaireHT = 0.0;

    // ✅ AJOUTÉ - TVA en pourcentage
    @Column(nullable = false)
    @Builder.Default
    private Double tva = 20.0;

    @Column(nullable = false)
    @Builder.Default
    private Double totalHT = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double totalTVA = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double totalTTC = 0.0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EtatProduit etatProduit = EtatProduit.BON_ETAT;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ActionRetour actionRetour = ActionRetour.REMBOURSEMENT;

    @Column(length = 500)
    @Builder.Default
    private String observation = "";

    @Column(name = "is_echange", nullable = false)
    @Builder.Default
    private Boolean isEchange = false;

    // ✅ Enums
    public enum EtatProduit {
        BON_ETAT,
        DEFECTUEUX,
        A_REPARER,
        DETRUIT
    }

    public enum ActionRetour {
        REMBOURSEMENT,
        ECHANGE,
        REPARATION,
        REINTEGRATION_STOCK
    }
}