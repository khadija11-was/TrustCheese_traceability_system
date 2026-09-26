package ma.trustCheese.TrustCheesebackend.dto.metrics;



import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricsResponse {


    private KpiResponse kpis;


    @Builder.Default
    private List<AlertResponse> alerts = List.of();


    @Builder.Default
    private List<ProductionResponse> productionsEnCours = List.of();


    @Builder.Default
    private List<CuveResponse> cuves = List.of();


    @Builder.Default
    private List<LivraisonResponse> livraisonsAvenir = List.of();


    @Builder.Default
    private List<RecentActivityResponse> recentActivities = List.of();


    // ============================================================
    // KPIs
    // ============================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class KpiResponse {


        private long productionsEnCours;


        private long lotsDisponibles;


        private long lotsBloques;


        private long livraisonsAujourdhui;
    }


    // ============================================================
    // ALERTES
    // ============================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AlertResponse {


        private String type;


        private String niveau;

        /**
         * Message affiché dans le dashboard.
         */
        private String message;


        private Long referenceId;


        private LocalDateTime date;
    }


    // ============================================================
    // PRODUCTIONS EN COURS
    // ============================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductionResponse {

        private Long id;

        /**
         * Numéro métier de la production.
         */
        private String numeroProduction;

        /**
         * Nom du produit fabriqué.
         */
        private String produitNom;

        /**
         * Statut actuel de la production.
         */
        private String statut;

        /**
         * Date réelle de début de production.
         */
        private LocalDateTime dateDebut;

        /**
         * Date prévue de début.
         */
        private LocalDateTime dateDebutPrevue;

        /**
         * Cuve utilisée par la production.
         */
        private String cuveNom;
    }


    // ============================================================
    // CUVES
    // ============================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CuveResponse {

        private Long id;

        /**
         * Nom de la cuve.
         */
        private String nom;

        /**
         * Capacité maximale en litres.
         */
        private BigDecimal capaciteMaxLitres;


        private String statutOperationnel;
    }


    // ============================================================
    // LIVRAISONS À VENIR
    // ============================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LivraisonResponse {

        private Long id;

        /**
         * Numéro métier de la livraison.
         */
        private String numeroLivraison;

        /**
         * Nom du client.
         */
        private String clientNom;

        /**
         * Date prévue de livraison.
         */
        private LocalDateTime dateLivraisonPrevue;

        /**
         * Statut actuel de la livraison.
         */
        private String statut;
    }


    // ============================================================
    // ACTIVITÉS RÉCENTES
    // ============================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentActivityResponse {

        private Long eventId;


        private String eventType;

        /**
         * Date et heure de l'événement.
         */
        private LocalDateTime eventDate;

        /**
         * Numéro métier du lot concerné.
         */
        private String numeroLot;

        /**
         * Nom de l'utilisateur ayant réalisé l'opération.
         */
        private String operatorNom;
    }
}


