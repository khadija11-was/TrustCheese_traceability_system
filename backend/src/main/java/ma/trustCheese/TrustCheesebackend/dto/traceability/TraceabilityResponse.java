package ma.trustCheese.TrustCheesebackend.dto.traceability;



import lombok.*;
import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraceabilityResponse {

    private LotInfo lot;

    private List<MatierePremiereInfo> matieresPremieres;

    private List<EventInfo> events;


    // =========================================================
    // LOT PRODUIT FINI
    // =========================================================

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


    // =========================================================
    // MATIERE PREMIERE UTILISEE
    // =========================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MatierePremiereInfo {

        private Long lotMPId;

        private String numeroLotMP;

        private Long matierePremiereId;

        private String codeMatierePremiere;

        private String nomMatierePremiere;

        /**
         * Quantité réellement consommée pendant la production.
         *
         * Cette valeur vient de MovementStock.quantite
         * et non de LotMP.quantite.
         */
        private BigDecimal quantiteUtilisee;

        private String uniteMesure;

        private Long fournisseurId;

        private String fournisseurNom;

        private LocalDateTime dateReception;
    }


    // =========================================================
    // EVENEMENT DE TRAÇABILITE
    // =========================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EventInfo {

        private Long eventId;

        private String eventType;

        private LocalDateTime eventDate;

        private String operatorNom;

        private JsonNode payload;
    }
}
