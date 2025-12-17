package com.example.AppPfa.DAO.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ligneFactureFournisseur")
public class LigneFactureFournisseurEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facture_fournisseur_id", nullable = false)
    @JsonIgnoreProperties({"ligneFactureFournisseurEntities"})
    @ToString.Exclude
    private FactureFournisseurEntity factureFournisseurEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ligne_bon_livraison_fournisseur_id", nullable = false)
    @JsonIgnoreProperties({"ligneFactureFournisseurEntities"})
    @ToString.Exclude
    private LigneBonDeLivraisonFournisseurEntity ligneBonDeLivraisonFournisseurEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ligne_commande_achats_id")
    @JsonIgnoreProperties({"commandeAchatsEntity"})
    private LigneCommandeAchatsEntity ligneCommandeAchatsEntity;

    // ✅ AJOUT - Champs manquants
    @Column(nullable = false)
    private Double totalHT = 0.0;

    @Column(nullable = false)
    private Double totalTVA = 0.0;

    @Column(nullable = false)
    private Double totalTTC = 0.0;
}