package ma.trustCheese.TrustCheesebackend.dto;

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
public class MatierePremiereResponse {

    private Long id;
    private String nom;
    private String code;
    private UniteMesure uniteMesure;
    private String description;
    private BigDecimal seuilStock;
}