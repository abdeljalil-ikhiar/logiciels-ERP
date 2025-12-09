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
@Table(name = "ligne_bon_de_livraison_fournisseur")
public class LigneBonDeLivraisonFournisseurEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ✅ Relation avec BonDeLivraisonFournisseur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bon_livraison_fournisseur_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private BonDeLivraisonFournisseurEntity bonDeLivraisonFournisseurEntity;

    // ✅ Référence vers LigneBonDeReception
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ligne_bon_reception_id")
    @JsonIgnoreProperties({"bonDeReceptionEntity", "ligneCommandeAchatsEntity"})
    private LigneBonDeReceptionEntities ligneBonDeReceptionEntity;

    // ✅ Référence vers LigneCommandeAchats
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ligne_commande_achats_id")
    @JsonIgnoreProperties({"commandeAchatsEntity", "ligneBonDeReceptionEntities"})
    private LigneCommandeAchatsEntity ligneCommandeAchatsEntity;

    // ✅ Totaux calculés
    @Column(nullable = false)
    private Double totalHT = 0.0;

    @Column(nullable = false)
    private Double totalTVA = 0.0;

    @Column(nullable = false)
    private Double totalTTC = 0.0;
}