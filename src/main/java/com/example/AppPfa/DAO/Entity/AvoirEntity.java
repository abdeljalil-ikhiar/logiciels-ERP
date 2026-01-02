package com.example.AppPfa.DAO.Entity;

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
@Builder
@Table(name = "avoir")
public class AvoirEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String numeroAvoir;

    @Column(nullable = false)
    private LocalDate dateAvoir;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAvoir typeAvoir;

    // ✅ Client (pour avoir client)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    @JsonIgnoreProperties({"avoirs", "commandeEntities", "devisEntities", "retoursProduit"})
    private ClientEntity client;

    // ✅ Fournisseur (pour avoir fournisseur)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fournisseur_id")
    @JsonIgnoreProperties({"commandeAchatsEntities", "retoursProduit", "avoirs"})
    private FournisseurEntity fournisseur;

    // ✅ Retour Produit lié (origine de l'avoir)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "retour_produit_id")
    @JsonIgnoreProperties({"avoir", "lignesRetour", "mouvementsStock"})
    @ToString.Exclude
    private RetourProduitEntity retourProduit;

    // ✅ Facture d'origine (pour avoir client)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facture_id")
    @JsonIgnoreProperties({"lignesFacture", "bonLivraison"})
    private FactureEntity facture;

    // ✅ Facture fournisseur d'origine (pour avoir fournisseur)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facture_fournisseur_id")
    @JsonIgnoreProperties({"ligneFactureFournisseurEntities", "bonDeLivraisonFournisseurEntity"})
    private FactureFournisseurEntity factureFournisseur;

    // ✅ Lignes d'avoir
    @OneToMany(mappedBy = "avoir", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"avoir"})
    @Builder.Default
    @ToString.Exclude
    private List<LigneAvoirEntity> lignesAvoir = new ArrayList<>();

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
    private StatutAvoir statut = StatutAvoir.EN_ATTENTE;

    @Column(length = 500)
    private String commentaire;

    // ✅ Enums
    public enum TypeAvoir {
        AVOIR_CLIENT,
        AVOIR_FOURNISSEUR
    }

    public enum StatutAvoir {
        EN_ATTENTE,
        VALIDE,
        UTILISE,
        REMBOURSE,
        ANNULE
    }

    @PrePersist
    public void prePersist() {
        if (this.numeroAvoir == null) {
            this.numeroAvoir = "AV-" + System.currentTimeMillis();
        }
        if (this.dateAvoir == null) {
            this.dateAvoir = LocalDate.now();
        }
    }
}