package ma.trustCheese.TrustCheesebackend.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FournisseurRequest {

    @NotBlank(message = "Nom est obligatoire")
    private String nom;

    @Email(message = "Format email invalide")
    private String email;

    private String telephone;

    private String adresse;
}
