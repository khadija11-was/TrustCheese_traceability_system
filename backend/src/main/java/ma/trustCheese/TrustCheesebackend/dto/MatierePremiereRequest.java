package ma.trustCheese.TrustCheesebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.trustCheese.TrustCheesebackend.enums.UniteMesure;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatierePremiereRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    private String code;

    private UniteMesure uniteMesure;

    private String description;
    private BigDecimal seuilStock;
}
