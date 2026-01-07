package com.example.AppPfa.DAO.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ligne_bon_livraison")
@Builder
public class LigneBonLivraisonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bon_livraison_id", nullable = false)
    @JsonIgnoreProperties({"lignesBonLivraison", "bonSortie"})
    @ToString.Exclude
    private BonLivraisonEntity bonLivraison;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ligne_bon_sortie_id")
    @JsonIgnoreProperties({"lignesBonLivraison", "bonSortie"})
    private LigneBonSortieEntity ligneBonSortie;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ligne_commande_id")
    @JsonIgnoreProperties({"lignesBonLivraison", "lignesBonSortie", "commandeEntity"})
    private LigneCommandeEntity ligneCommande;

    @OneToMany(
            mappedBy = "ligneBonLivraison",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties({"ligneBonLivraison"})
    @ToString.Exclude
    private List<LigneFactureEntity> lignesFacture = new ArrayList<>();

    @Column(nullable = false)
    private Double totalHT = 0.0;

    @Column(nullable = false)
    private Double totalTVA = 0.0;

    @Column(nullable = false)
    private Double totalTTC = 0.0;

    // ✅ NOUVEAU: Stocker la remise appliquée pour traçabilité
    @Column(nullable = false)
    private Double remiseAppliquee = 0.0;

    // ✅ NOUVEAU: Montant de la remise
    @Column(nullable = false)
    private Double montantRemise = 0.0;
}