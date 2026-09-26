package ma.trustCheese.TrustCheesebackend.entity;



import jakarta.persistence.*;
import lombok.*;
import ma.trustCheese.TrustCheesebackend.enums.DecisionControleQualite;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "controle_qualite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ControleQualite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String numeroControle;

    @Column(nullable = false)
    private LocalDateTime dateControle;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal temperature;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal ph;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal extraitSec;

    @Column(length = 100)
    private String texture;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DecisionControleQualite decision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_produit_fini_id", nullable = false)
    private LotProduitFini lotProduitFini;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;
}

