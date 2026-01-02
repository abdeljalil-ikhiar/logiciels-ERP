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
@Table(name = "retour_produit")
public class RetourProduitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String numeroRetour;

    @Column(nullable = false)
    private LocalDate dateRetour;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeRetour typeRetour;

    // ✅ Client uniquement si RETOUR_CLIENT
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    @JsonIgnoreProperties({"retoursProduit"})
    private ClientEntity client;

    // ✅ Fournisseur uniquement si RETOUR_FOURNISSEUR
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fournisseur_id")
    @JsonIgnoreProperties({"retoursProduit"})
    private FournisseurEntity fournisseur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bon_livraison_id")
    @JsonIgnoreProperties({"retoursProduit"})
    private BonLivraisonEntity bonLivraison;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bon_reception_id")
    @JsonIgnoreProperties({"retoursProduit"})
    private BonDeReceptionEntity bonDeReception;

    @OneToMany(
            mappedBy = "retourProduit",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<LigneRetourEntity> lignesRetour = new ArrayList<>();

    // ✅ UN seul avoir possible
    @OneToOne(
            mappedBy = "retourProduit",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private AvoirEntity avoir;

    @OneToMany(
            mappedBy = "retourProduit",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<MouvementStockEntity> mouvementsStock = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutRetour statut = StatutRetour.EN_ATTENTE;

    private String motifRetour;

    private Double totalHT = 0.0;
    private Double totalTVA = 0.0;
    private Double totalTTC = 0.0;

    public enum TypeRetour {
        RETOUR_CLIENT,
        RETOUR_FOURNISSEUR
    }

    public enum StatutRetour {
        EN_ATTENTE,
        VALIDE,
        AVOIR_GENERE,
        REMBOURSE,
        ECHANGE,
        ANNULE
    }

    @PrePersist
    public void prePersist() {
        if (numeroRetour == null) {
            numeroRetour = "RET-" + System.currentTimeMillis();
        }
        if (dateRetour == null) {
            dateRetour = LocalDate.now();
        }
    }
}
