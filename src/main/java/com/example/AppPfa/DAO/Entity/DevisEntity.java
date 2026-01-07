package com.example.AppPfa.DAO.Entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "devis")
public class DevisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "devis_id")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String numeroDevis;

    @Column(nullable = false)
    private LocalDate dateDevis = LocalDate.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnoreProperties({"devisEntities"})
    @ToString.Exclude
    private ClientEntity client;

    @OneToMany(mappedBy = "devis", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)  // ✅ زدنا EAGER
    private List<LigneDevisEntity> lignesDevis = new ArrayList<>();

    @Column(nullable = false)
    private Double totalHT = 0.0;

    @Column(nullable = false)
    private Double totalTTC = 0.0;
    @Column(nullable = false)
    private Double totalRemise = 0.0;
}