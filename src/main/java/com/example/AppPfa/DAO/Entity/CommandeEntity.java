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
@Table(name = "commande")
@Builder
public class CommandeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_commande")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String numerocommande;

    @Column(nullable = false)
    private LocalDate datecommande = LocalDate.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnoreProperties({"devisEntities", "commandeEntities"})
    private ClientEntity client;

    @Column(nullable = false)
    private Double totalHT = 0.0;

    @Column(nullable = false)
    private Double totalTTC = 0.0;

    @Column(nullable = false)
    private Double totalRemise = 0.0;

    @OneToMany(mappedBy = "commandeEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<LigneCommandeEntity> lignesCommande = new ArrayList<>();

    @OneToMany(mappedBy = "commandeEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<BonSortieEntity> bonSortieEntities = new ArrayList<>();
}