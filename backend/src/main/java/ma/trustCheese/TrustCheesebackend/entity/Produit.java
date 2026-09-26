package ma.trustCheese.TrustCheesebackend.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "produit")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String description;

    private BigDecimal quantiteStandardLot;
    private Integer dureeConservationJours;

    @Column(length = 500)
    private String imageUrl;
}
