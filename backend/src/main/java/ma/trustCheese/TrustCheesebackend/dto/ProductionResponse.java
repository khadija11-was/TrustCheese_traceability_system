package ma.trustCheese.TrustCheesebackend.dto;


import lombok.*;
import ma.trustCheese.TrustCheesebackend.enums.StatutProduction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionResponse {

    private Long id;

    private String numeroProduction;

    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    private Integer nombreLotsProduits;

    private StatutProduction statut;

    private Long produitId;

    private String produitNom;
    private LocalDateTime dateDebutPrevue;

    private LocalDateTime dateFinPrevue;

    private Long cuveId;

    private String cuveNom;

    private BigDecimal temperatureCuve;

    private BigDecimal phCuve;

    private Long operateurId;
    private String operateurNom;

}
