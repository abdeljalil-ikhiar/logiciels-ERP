package com.example.AppPfa.DAO.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "facture_fournisseur")
public class FactureFournisseurEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String numeroFacture;

    @Column(nullable = false)
    private LocalDate dateFacture;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bon_livraison_fournisseur_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"bonDeReceptionEntity", "ligneBonDeLivraisonEntities"})
    private BonDeLivraisonFournisseurEntity bonDeLivraisonFournisseurEntity;

    @OneToMany(
            mappedBy = "factureFournisseurEntity",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties({"factureFournisseurEntity"})
    @ToString.Exclude
    @Builder.Default
    private List<LigneFactureFournisseurEntity> ligneFactureFournisseurEntities = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private Double totalHT = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double totalTVA = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double totalTTC = 0.0;

    // ✅ PDF - Stocké en base de données
    @Lob
    @Column(name = "pdf_file", columnDefinition = "LONGBLOB")
    @JsonIgnore
    @ToString.Exclude
    private byte[] pdfFile;

    // ✅ Nom original du fichier PDF
    @Column(name = "pdf_file_name")
    private String pdfFileName;

    // ✅ Date d'upload du PDF
    @Column(name = "pdf_upload_date")
    private LocalDateTime pdfUploadDate;

    // ✅ Méthode utilitaire pour vérifier si un PDF existe
    public boolean hasPdf() {
        return pdfFile != null && pdfFile.length > 0;
    }
}