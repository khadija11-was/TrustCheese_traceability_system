package ma.trustCheese.TrustCheesebackend.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import ma.trustCheese.TrustCheesebackend.enums.DecisionControleQualite;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ControleQualiteRequest {

    @NotNull(message = "L'identifiant de l'affinage est obligatoire")
    private Long affinageId;

    @NotNull(message = "La température est obligatoire")
    private BigDecimal temperature;

    @NotNull(message = "Le pH est obligatoire")
    private BigDecimal ph;

    @NotNull(message = "L'extrait sec est obligatoire")
    private BigDecimal extraitSec;

    private String texture;

    @NotNull(message = "La décision du contrôle qualité est obligatoire")
    private DecisionControleQualite decision;



    private String notes;
}

