package ma.trustCheese.TrustCheesebackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "ligne_livraison",
        indexes = {
                @Index(
                        name = "idx_ligne_livraison_livraison",
                        columnList = "livraison_id"
                ),
                @Index(
                        name = "idx_ligne_livraison_produit",
                        columnList = "produit_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneLivraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "livraison_id",
            nullable = false
    )
    private Livraison livraison;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "produit_id",
            nullable = false
    )
    private Produit produit;


    @Column(
            nullable = false
    )
    private Integer quantiteLots;


    @ManyToMany
    @JoinTable(
            name = "ligne_livraison_lot",
            joinColumns = @JoinColumn(
                    name = "ligne_livraison_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "lot_produit_fini_id"
            ),
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uk_ligne_livraison_lot",
                            columnNames = {
                                    "ligne_livraison_id",
                                    "lot_produit_fini_id"
                            }
                    )
            }
    )
    @Builder.Default
    private List<LotProduitFini> lotsProduitFini = new ArrayList<>();


}