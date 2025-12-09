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
@Table(name = "facture")
@Builder
public class FactureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String numeroFacture;

    @Column(nullable = false)
    private LocalDate dateFacture;

    // ✅ Owning side - pas de cascade vers BonLivraison
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bon_livraison_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"facture", "lignesBonLivraison", "bonSortie"})
    private BonLivraisonEntity bonLivraison;

    // ✅ Cascade ALL + orphanRemoval pour supprimer les lignes automatiquement
    @OneToMany(
            mappedBy = "facture",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties({"facture"})
    @ToString.Exclude
    private List<LigneFactureEntity> lignesFacture = new ArrayList<>();

    @Column(nullable = false)
    private Double totalHT = 0.0;

    @Column(nullable = false)
    private Double totalTVA = 0.0;

    @Column(nullable = false)
    private Double totalTTC = 0.0;
}