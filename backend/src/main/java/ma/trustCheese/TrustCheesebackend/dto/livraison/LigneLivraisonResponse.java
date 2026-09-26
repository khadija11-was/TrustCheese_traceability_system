package ma.trustCheese.TrustCheesebackend.dto.livraison;



import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneLivraisonResponse {

    private Long id;

    private Long produitId;

    private String produitNom;

    private Integer quantiteLots;

    private List<LotInfo> lots;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LotInfo {

        private Long id;

        private String numeroLot;

        private LocalDate dateExpiration;

        private String statut;
    }
}