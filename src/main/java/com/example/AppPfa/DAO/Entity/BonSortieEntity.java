// BonSortieEntity.java
package com.example.AppPfa.DAO.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "bon_sortie")
@Builder
public class BonSortieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String numeroBonSortie;
    private LocalDate dateSortie;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_commande", nullable = false)
    @JsonIgnoreProperties({"bonSortieEntities", "lignesCommande"})
    private CommandeEntity commandeEntity;

    @OneToMany(mappedBy = "bonSortie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"bonSortie"})
    @ToString.Exclude
    private List<LigneBonSortieEntity> ligneBonSortieEntities = new ArrayList<>();

    // ✅ CORRECTION ICI - Enlever cascade et orphanRemoval
    @OneToOne(mappedBy = "bonSortie", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private BonLivraisonEntity bonLivraison;
}