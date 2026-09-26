package ma.trustCheese.TrustCheesebackend.dto;


import lombok.*;
import ma.trustCheese.TrustCheesebackend.enums.EtatStock;
import ma.trustCheese.TrustCheesebackend.enums.UniteMesure;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LotMPResponse {

    private Long id;

    private String numeroLot;

    // Matière première
    private Long matierePremiereId;
    private String matierePremiereNom;
    private UniteMesure uniteMesure;

    // Fournisseur
    private Long fournisseurId;
    private String fournisseurNom;

    // Réception
    private BigDecimal quantite;
    private LocalDateTime dateReception;

    // Stock calculé
    private BigDecimal quantiteRestante;
    private BigDecimal seuilStock;
    private EtatStock etatStock;
}
