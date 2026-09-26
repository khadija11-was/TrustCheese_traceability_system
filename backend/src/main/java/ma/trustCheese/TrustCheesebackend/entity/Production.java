package ma.trustCheese.TrustCheesebackend.entity;


import jakarta.persistence.*;
import lombok.*;
import ma.trustCheese.TrustCheesebackend.enums.StatutProduction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "production")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Production {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroProduction;

    @Column
    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    @Column
    private Integer nombreLotsProduits;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutProduction statut;

    @Column(precision = 5, scale = 2)
    private BigDecimal temperatureCuve;

    @Column(precision = 4, scale = 2)
    private BigDecimal phCuve;

    @OneToMany(mappedBy = "production")
    private List<MovementStock> mouvementsStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operateur_id")
    private Utilisateur operateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    private LocalDateTime dateDebutPrevue;
    private LocalDateTime dateFinPrevue;

    @OneToMany(
            mappedBy = "production",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<LotProduitFini> lotsProduitsFinis = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuve_id")
    private Cuve cuve;
}
