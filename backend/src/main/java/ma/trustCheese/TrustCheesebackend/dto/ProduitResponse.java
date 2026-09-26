package ma.trustCheese.TrustCheesebackend.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProduitResponse {

    private Long id;

    private String nom;

    private String description;

    private BigDecimal quantiteStandardLot;

    private Integer dureeConservationJours;

    private String imageUrl;
}

