package ma.trustCheese.TrustCheesebackend.dto.livraison;



import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivraisonRequest {

    @NotNull(message = "Le client est obligatoire")
    private Long clientId;

    private LocalDateTime dateLivraisonPrevue;

    private String adresseLivraison;

    private String ville;

    private String pays;

    private BigDecimal temperatureMoyenneCamion;


    @NotEmpty(message = "La livraison doit contenir au moins une ligne")
    @Valid
    private List<LigneLivraisonRequest> lignes;
}