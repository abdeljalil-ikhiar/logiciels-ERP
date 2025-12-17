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
@Table(name = "inventaire")
public class InventaireEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String numeroInventaire;

    @Column(nullable = false)
    private LocalDate dateInventaire;

    @OneToMany(
            mappedBy = "inventaire",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @JsonIgnoreProperties("inventaire")
    private List<LigneInventaireEntity> ligneInventaireEntities = new ArrayList<>();
}
