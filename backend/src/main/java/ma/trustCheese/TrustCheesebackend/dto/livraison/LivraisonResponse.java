package ma.trustCheese.TrustCheesebackend.dto.livraison;



import lombok.*;
import ma.trustCheese.TrustCheesebackend.enums.StatutLivraison;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivraisonResponse {

    private Long id;

    private String numeroLivraison;

    private Long clientId;

    private String clientNom;

    private LocalDateTime dateLivraisonPrevue;

    private LocalDateTime dateLivraisonReelle;

    private StatutLivraison statut;

    private String adresseLivraison;

    private String ville;

    private String pays;

    private BigDecimal temperatureMoyenneCamion;

    private Long responsableId;

    private String responsableNom;

    private List<LigneLivraisonResponse> lignes;

}