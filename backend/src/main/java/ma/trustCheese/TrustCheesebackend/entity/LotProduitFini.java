package ma.trustCheese.TrustCheesebackend.entity;



import jakarta.persistence.*;
import lombok.*;
import ma.trustCheese.TrustCheesebackend.enums.StatutLotProduitFini;
import ma.trustCheese.TrustCheesebackend.enums.StatutProduction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lot_produit_fini")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LotProduitFini {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroLot;


    private LocalDate dateExpiration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutLotProduitFini statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_id", nullable = false)
    private Production production;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affinage_id")
    private Affinage affinage;

    @OneToMany(mappedBy = "lotProduitFini")
    private List<ControleQualite> controlesQualite;



}

