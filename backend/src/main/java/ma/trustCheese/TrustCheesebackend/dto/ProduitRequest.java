package ma.trustCheese.TrustCheesebackend.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProduitRequest {

    @NotBlank(message = "Le nom du produit est obligatoire")
    private String nom;

    private String description;

    @NotNull(message = "La quantité standard par lot est obligatoire")
    @DecimalMin(value = "0.01", message = "La quantité standard doit être supérieure à 0")
    private BigDecimal quantiteStandardLot;

    @NotNull(message = "La durée de conservation est obligatoire")
    private Integer dureeConservationJours;

    private MultipartFile image;
}
