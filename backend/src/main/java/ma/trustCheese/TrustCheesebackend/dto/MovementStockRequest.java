package ma.trustCheese.TrustCheesebackend.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementStockRequest {

    @NotNull(message = "La quantité est obligatoire")
    @DecimalMin(
            value = "0.01",
            message = "La quantité doit être supérieure à 0"
    )
    private BigDecimal quantite;

    @NotNull(message = "La date du mouvement est obligatoire")
    private LocalDateTime dateMouvement;

    @NotNull(message = "Le lot MP est obligatoire")
    private Long lotMPId;

    @NotNull(message = "La production est obligatoire")
    private Long productionId;
}
