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
@Table(name = "bon_de_livraison_fournisseur")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BonDeLivraisonFournisseurEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String numeroLivraison;

    @Column(name = "numero_bl_fournisseur")
    private String numeroBLFournisseur;

    @Column(nullable = false)
    private LocalDate dateLivraison;

    @Column(name = "date_bl_fournisseur")
    private LocalDate dateBLFournisseur;

    // ✅ Relation avec Bon de Réception
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bon_reception_id", nullable = false)
    @JsonIgnoreProperties({
            "ligneBonDeReceptionEntities",
            "bonDeLivraisonFournisseurEntity",
            "commandeAchatsEntity",
            "hibernateLazyInitializer",
            "handler"
    })
    @ToString.Exclude
    private BonDeReceptionEntity bonDeReceptionEntity;

    // ✅ Relation avec Fournisseur
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fournisseur_id")
    @JsonIgnoreProperties({
            "commandeAchatsEntities",
            "hibernateLazyInitializer",
            "handler"
    })
    private FournisseurEntity fournisseurEntity;

    // ✅ Lignes du Bon de Livraison
    @OneToMany(mappedBy = "bonDeLivraisonFournisseurEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({
            "bonDeLivraisonFournisseurEntity",
            "hibernateLazyInitializer",
            "handler"
    })
    @Builder.Default
    private List<LigneBonDeLivraisonFournisseurEntity> ligneBonDeLivraisonEntities = new ArrayList<>();

    // ✅ Totaux
    @Column(nullable = false)
    @Builder.Default
    private Double totalHT = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double totalTVA = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double totalTTC = 0.0;

    // ✅ Statut
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutLivraison statut = StatutLivraison.EN_ATTENTE;

    @Column(length = 500)
    private String commentaire;

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

    // ✅ Enum Statut
    public enum StatutLivraison {
        EN_ATTENTE,
        PARTIELLEMENT_LIVRE,
        COMPLETEMENT_LIVRE,
        ANNULE
    }

    // ✅ Méthode utilitaire pour vérifier si un PDF existe
    public boolean hasPdf() {
        return pdfFile != null && pdfFile.length > 0;
    }
}