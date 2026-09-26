package ma.trustCheese.TrustCheesebackend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.trustCheese.TrustCheesebackend.entity.Fournisseur;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FournisseurResponse {

    private Long id;
    private String codeFournisseur;
    private String nom;
    private String email;
    private String telephone;
    private String adresse;
}
