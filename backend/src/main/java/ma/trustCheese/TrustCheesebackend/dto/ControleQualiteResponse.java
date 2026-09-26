package ma.trustCheese.TrustCheesebackend.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import ma.trustCheese.TrustCheesebackend.enums.DecisionControleQualite;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ControleQualiteResponse {

    private Long id;

    private List<String> numerosControles;

    private LocalDateTime dateControle;

    private BigDecimal temperature;

    private BigDecimal ph;

    private BigDecimal extraitSec;

    private String texture;

    private String notes;

    @NotNull(message = "La décision du contrôle qualité est obligatoire")
    private DecisionControleQualite decision;


    private int nombreLots;

    private Long utilisateurId;
    private String utilisateurNom;
    private Long affinageId;
    private List<String> numerosLots;
}

