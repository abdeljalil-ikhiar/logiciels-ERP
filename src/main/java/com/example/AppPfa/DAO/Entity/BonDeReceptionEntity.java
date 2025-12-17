package com.example.AppPfa.DAO.Entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Bon_de_réception")
public class BonDeReceptionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String numeroreception;
  private LocalDate date;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "commande_achats_id")
  private CommandeAchatsEntity commandeAchatsEntity;

  @OneToMany(mappedBy = "bonDeReceptionEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<LigneBonDeReceptionEntities> ligneBonDeReceptionEntities = new ArrayList<>();
  // ✅ Relation ONE-TO-ONE avec BonDeLivraisonFournisseur
  @OneToOne(mappedBy = "bonDeReceptionEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnoreProperties({"bonDeReceptionEntity"})
  private BonDeLivraisonFournisseurEntity bonDeLivraisonFournisseurEntity;

}

