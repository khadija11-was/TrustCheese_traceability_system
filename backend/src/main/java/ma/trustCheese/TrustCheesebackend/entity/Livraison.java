package ma.trustCheese.TrustCheesebackend.entity;



import jakarta.persistence.*;
import lombok.*;
import ma.trustCheese.TrustCheesebackend.enums.StatutLivraison;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(
        name = "livraison",
        indexes = {
                @Index(
                        name = "idx_livraison_numero",
                        columnList = "numero_livraison"
                ),
                @Index(
                        name = "idx_livraison_date",
                        columnList = "date_livraison"
                ),
                @Index(
                        name = "idx_livraison_client",
                        columnList = "client_id"
                ),
                @Index(
                        name = "idx_livraison_statut",
                        columnList = "statut"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Numéro unique de la livraison.
     * Exemple : LIV-2026-0001
     */
    @Column(
            name = "numero_livraison",
            nullable = false,
            unique = true,
            length = 50
    )
    private String numeroLivraison;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "client_id",
            nullable = false
    )
    private Client client;


    @Column(name = "date_livraison_prevue")
    private LocalDateTime dateLivraisonPrevue;


    @Column(name = "date_livraison_reelle")
    private LocalDateTime dateLivraisonReelle;

    /**
     * Statut actuel de la livraison.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "statut",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private StatutLivraison statut = StatutLivraison.PLANIFIEE;


    @Column(
            name = "adresse_livraison",
            nullable = false,
            length = 255
    )
    private String adresseLivraison;

    @Column(length = 100)
    private String ville;

    @Column(length = 100)
    private String pays;

        @Column(precision = 5, scale = 2)
        private BigDecimal temperatureMoyenneCamion;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Utilisateur responsable;

    @OneToMany(
            mappedBy = "livraison",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<LigneLivraison> lignes = new ArrayList<>();
}