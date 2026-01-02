package com.example.AppPfa.DAO.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "etatdustock")
public class EtatduStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    @JsonIgnoreProperties("etatduStocks")
    @ToString.Exclude
    private ProduitEntity produit;

    @Column(name = "stock_min", nullable = false)
    private Double stockMin;

    @Column(name = "stock_max", nullable = false)
    private Double stockMax;

    // ✅ AJOUTÉ : LE CHAMP stockReel QUI MANQUAIT !
    @Column(name = "stock_reel", nullable = false)
    private Double stockReel;

    @Column(name = "zone_stock", length = 100)
    private String zoneStock;

    @PrePersist
    private void prePersist() {
        if (stockReel == null) stockReel = 0.0;
        if (stockMin == null) stockMin = 0.0;
        if (stockMax == null) stockMax = 0.0;
    }
}