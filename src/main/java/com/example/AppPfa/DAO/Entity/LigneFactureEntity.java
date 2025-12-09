package com.example.AppPfa.DAO.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ligne_facture")
@Builder
public class LigneFactureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ✅ Relation principale avec Facture
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facture_id", nullable = false)
    @JsonIgnoreProperties({"lignesFacture", "bonLivraison"})
    @ToString.Exclude
    private FactureEntity facture;

    // ✅ Référence vers LigneBonLivraison (sans cascade inverse)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ligne_bon_livraison_id")
    @JsonIgnoreProperties({"lignesFacture", "bonLivraison"})
    private LigneBonLivraisonEntity ligneBonLivraison;

    // ✅ Référence vers LigneCommande (sans cascade inverse)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ligne_commande_id")
    @JsonIgnoreProperties({"lignesFacture", "lignesBonLivraison", "commandeEntity"})
    private LigneCommandeEntity ligneCommande;

    @Column(nullable = false)
    private Double totalHT = 0.0;

    @Column(nullable = false)
    private Double totalTVA = 0.0;

    @Column(nullable = false)
    private Double totalTTC = 0.0;
}