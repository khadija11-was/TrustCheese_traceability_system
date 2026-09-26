package ma.trustCheese.TrustCheesebackend.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffinageRequest {

    @NotNull(message = "L'identifiant de la production est obligatoire")
    private Long productionId;

    @NotNull(message = "La température est obligatoire")
    private BigDecimal temperature;

    @NotNull(message = "L'humidité est obligatoire")
    private BigDecimal humidite;

    private String observations;
}
