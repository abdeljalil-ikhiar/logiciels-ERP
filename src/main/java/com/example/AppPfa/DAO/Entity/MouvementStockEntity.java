package com.example.AppPfa.DAO.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "mouvement_stock")
public class MouvementStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMouvement typeMouvement;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produit_id", nullable = false)
    @JsonIgnoreProperties({"mouvementsStock", "ligneDevisEntities", "ligneCommandeEntities"})
    private ProduitEntity produit;

    @Column(nullable = false)
    private Double quantite;

    @Column(nullable = false)
    private Double quantiteAvant;

    @Column(nullable = false)
    private Double quantiteApres;

    private Double prixUnitaire;

    @Column(length = 500)
    private String motif;

    @Column(nullable = false)
    private LocalDateTime dateMouvement;

    // ✅ Références vers les documents sources
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bon_reception_id")
    @JsonIgnoreProperties({"ligneBonDeReceptionEntities"})
    @ToString.Exclude
    private BonDeReceptionEntity bonDeReception;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bon_sortie_id")
    @JsonIgnoreProperties({"ligneBonSortieEntities"})
    @ToString.Exclude
    private BonSortieEntity bonSortie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retour_produit_id")
    @JsonIgnoreProperties({"lignesRetour", "mouvementsStock"})
    @ToString.Exclude
    private RetourProduitEntity retourProduit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventaire_id")
    @JsonIgnoreProperties({"ligneInventaireEntities"})
    @ToString.Exclude
    private InventaireEntity inventaire;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutMouvement statut = StatutMouvement.VALIDE;

    // ✅ Enum Type Mouvement
    public enum TypeMouvement {
        ENTREE_ACHAT,           // Réception fournisseur
        SORTIE_VENTE,           // Livraison client
        RETOUR_CLIENT,          // Retour d'un client
        RETOUR_FOURNISSEUR,     // Retour vers fournisseur
        AJUSTEMENT_POSITIF,     // Inventaire +
        AJUSTEMENT_NEGATIF,     // Inventaire -
        PERTE,                  // Produit perdu/cassé
        ENTREE_INITIALE         // Stock initial
    }

    // ✅ Enum Statut
    public enum StatutMouvement {
        EN_ATTENTE,
        VALIDE,
        ANNULE
    }

    @PrePersist
    public void prePersist() {
        if (this.dateMouvement == null) {
            this.dateMouvement = LocalDateTime.now();
        }
        if (this.reference == null) {
            this.reference = "MVT-" + System.currentTimeMillis();
        }
    }
}