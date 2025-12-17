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
@Builder
@Table(name = "ligne_bon_livraison_fournisseur")
public class LigneBonDeLivraisonFournisseurEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bon_livraison_fournisseur_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private BonDeLivraisonFournisseurEntity bonDeLivraisonFournisseurEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ligne_bon_reception_id")
    @JsonIgnoreProperties({"bonDeReceptionEntity"})
    private LigneBonDeReceptionEntities ligneBonDeReceptionEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ligne_commande_achats_id")
    @JsonIgnoreProperties({"commandeAchatsEntity"})
    private LigneCommandeAchatsEntity ligneCommandeAchatsEntity;

    @OneToMany(
            mappedBy = "ligneBonDeLivraisonFournisseurEntity",  // ✅ Corrigé
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties({"ligneBonDeLivraisonFournisseurEntity"})
    @ToString.Exclude
    @Builder.Default
    private List<LigneFactureFournisseurEntity> ligneFactureFournisseurEntities = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private Double totalHT = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double totalTVA = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double totalTTC = 0.0;
}