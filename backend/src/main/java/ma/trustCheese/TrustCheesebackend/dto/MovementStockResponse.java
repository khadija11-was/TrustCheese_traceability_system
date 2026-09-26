package ma.trustCheese.TrustCheesebackend.dto;



import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementStockResponse {

    private Long id;

    private BigDecimal quantite;

    private LocalDateTime dateMouvement;

    private Long lotMPId;
    private String numeroLot;

    private Long productionId;

    private BigDecimal quantiteRestante;
}
