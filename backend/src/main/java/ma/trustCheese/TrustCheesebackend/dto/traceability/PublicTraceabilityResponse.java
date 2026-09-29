package ma.trustCheese.TrustCheesebackend.dto.traceability;



import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicTraceabilityResponse {

    /**
     * Informations générales du produit et du lot.
     */
    private ProduitInfo produit;

    /**
     * Informations générales sur le lot.
     */
    private LotInfo lot;

    /**
     * Matières premières utilisées dans la production.
     *
     * On expose uniquement leur nom.
     * Les quantités exactes et les fournisseurs ne sont pas exposés
     * dans la page publique.
     */
    @Builder.Default
    private List<MatierePremiereInfo> matieresPremieres = List.of();

    /**
     * Étapes de traçabilité visibles par le consommateur.
     */
    @Builder.Default
    private List<EtapeTraceabiliteInfo> etapes = List.of();


    // =========================================================
    // PRODUIT
    // =========================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProduitInfo {

        private Long id;

        private String nom;

        private String description;

        /**
         * URL de l'image du produit.
         */
        private String imageUrl;
    }


    // =========================================================
    // LOT
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

        /**
         * Date de fin réelle de la production.
         * Elle représente la date de production du lot.
         */
        private LocalDateTime dateProduction;
    }


    // =========================================================
    // MATIÈRES PREMIÈRES
    // =========================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MatierePremiereInfo {

        private Long id;

        private String code;

        private String nom;

        private String uniteMesure;
    }


    // =========================================================
    // ÉTAPES DE TRAÇABILITÉ
    // =========================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EtapeTraceabiliteInfo {

        private String type;

        private String titre;

        private LocalDateTime date;

        private String statut;
    }
}
