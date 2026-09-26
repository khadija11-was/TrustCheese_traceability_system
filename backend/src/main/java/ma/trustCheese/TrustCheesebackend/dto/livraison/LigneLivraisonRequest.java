package ma.trustCheese.TrustCheesebackend.dto.livraison;



import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneLivraisonRequest {

    @NotNull(message = "Le produit est obligatoire")
    private Long produitId;

    @NotNull(message = "La quantité de lots est obligatoire")
    @Positive(message = "La quantité de lots doit être supérieure à zéro")
    private Integer quantiteLots;

}