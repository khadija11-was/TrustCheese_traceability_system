package ma.trustCheese.TrustCheesebackend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LotMPRequest {

    @NotNull(message = "La quantité est obligatoire")
    @DecimalMin(value = "0.01", message = "La quantité doit être supérieure à 0")
    private BigDecimal quantite;

    @NotNull(message = "La date de réception est obligatoire")
    private LocalDateTime dateReception;


    @NotNull(message = "Le fournisseur est obligatoire")
    private Long fournisseurId;

    @NotNull(message = "La matière première est obligatoire")
    private Long matierePremiereId;
}