package ma.trustCheese.TrustCheesebackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionMesureRequest {

    @NotNull(message = "La température de cuve est obligatoire")
    private BigDecimal temperatureCuve;

    @NotNull(message = "Le pH de cuve est obligatoire")
    private BigDecimal phCuve;
}
