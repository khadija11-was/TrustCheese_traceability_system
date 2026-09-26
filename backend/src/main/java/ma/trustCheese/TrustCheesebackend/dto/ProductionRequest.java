package ma.trustCheese.TrustCheesebackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.trustCheese.TrustCheesebackend.enums.StatutProduction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionRequest {

    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    private Integer nombreLotsProduits;


    @NotNull
    private Long produitId;
    @NotNull(message = "La date de début prévue est obligatoire")
    private LocalDateTime dateDebutPrevue;

    private LocalDateTime dateFinPrevue;

    private Long cuveId;

    private BigDecimal temperatureCuve;

    private BigDecimal phCuve;

}