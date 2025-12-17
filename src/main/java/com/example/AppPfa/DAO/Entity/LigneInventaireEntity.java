package com.example.AppPfa.DAO.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ligneinventaire")
public class LigneInventaireEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    @JsonIgnoreProperties("ligneInventaireEntities")
    private ProduitEntity produit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventaire_id", nullable = false)
    @JsonIgnoreProperties("ligneInventaireEntities")
    private InventaireEntity inventaire;

    @Column(nullable = false)
    private Double qteinventaire;

    private String namezone;
    private String observations;
}
