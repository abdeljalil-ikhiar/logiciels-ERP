package com.example.AppPfa.DAO.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fournisseur")
public class FournisseurEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nomfournisseur;

    @Column(nullable = false, unique = true)
    private String icefournisseur;

    private String adressfournisseur;
    private String nomachteur;

    @Column(nullable = false, unique = true)
    private String telephone;

    private String actviteFournisseur;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "fournisseurEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<CommandeAchatsEntity> commandeAchatsEntities;
}