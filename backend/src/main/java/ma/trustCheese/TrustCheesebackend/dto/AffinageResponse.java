package ma.trustCheese.TrustCheesebackend.dto;



import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffinageResponse {

    private Long id;

    private String numeroAffinage;

    private LocalDateTime dateDebutReelle;

    private LocalDateTime dateFinReelle;

    private BigDecimal temperature;

    private BigDecimal humidite;

    private String observations;

    private String statut;

    private Integer nombreLots;

    private Long productionId;

    private String numeroProduction;
}