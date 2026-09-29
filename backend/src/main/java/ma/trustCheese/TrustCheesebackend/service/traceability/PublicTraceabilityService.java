package ma.trustCheese.TrustCheesebackend.service.traceability;



import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.traceability.PublicTraceabilityResponse;
import ma.trustCheese.TrustCheesebackend.entity.LotProduitFini;
import ma.trustCheese.TrustCheesebackend.entity.MovementStock;
import ma.trustCheese.TrustCheesebackend.entity.TraceabilityEvent;
import ma.trustCheese.TrustCheesebackend.enums.TypeEvenementTraceabilite;
import ma.trustCheese.TrustCheesebackend.repository.LotProduitFiniRepository;
import ma.trustCheese.TrustCheesebackend.repository.TraceabilityEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicTraceabilityService {

    private final LotProduitFiniRepository lotProduitFiniRepository;
    private final TraceabilityEventRepository traceabilityEventRepository;

    /**
     * Récupère les informations publiques de traçabilité
     * d'un lot à partir de son numéro métier.
     *
     * @param numeroLot numéro du lot de produit fini
     * @return informations publiques de traçabilité
     */
    public PublicTraceabilityResponse getPublicTraceability(String numeroLot) {

        if (numeroLot == null || numeroLot.isBlank()) {
            throw new IllegalArgumentException(
                    "Le numéro de lot est obligatoire."
            );
        }

        String normalizedNumeroLot = numeroLot.trim();

        LotProduitFini lot = lotProduitFiniRepository
                .findByNumeroLot(normalizedNumeroLot)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucun lot trouvé pour le numéro : "
                                + normalizedNumeroLot
                ));

        return buildPublicResponse(lot);
    }

    /**
     * Construit la réponse destinée au public.
     */
    private PublicTraceabilityResponse buildPublicResponse(
            LotProduitFini lot
    ) {

        PublicTraceabilityResponse.ProduitInfo produitInfo =
                buildProduitInfo(lot);

        PublicTraceabilityResponse.LotInfo lotInfo =
                buildLotInfo(lot);

        List<PublicTraceabilityResponse.MatierePremiereInfo>
                matieresPremieres = buildMatieresPremieres(lot);

        List<PublicTraceabilityResponse.EtapeTraceabiliteInfo>
                etapes = buildEtapes(lot);

        return PublicTraceabilityResponse.builder()
                .produit(produitInfo)
                .lot(lotInfo)
                .matieresPremieres(matieresPremieres)
                .etapes(etapes)
                .build();
    }

    /**
     * Informations publiques du produit.
     */
    private PublicTraceabilityResponse.ProduitInfo buildProduitInfo(
            LotProduitFini lot
    ) {

        if (lot.getProduction() == null
                || lot.getProduction().getProduit() == null) {
            return null;
        }

        var produit = lot.getProduction().getProduit();

        return PublicTraceabilityResponse.ProduitInfo.builder()
                .id(produit.getId())
                .nom(produit.getNom())
                .description(produit.getDescription())
                .imageUrl(produit.getImageUrl())
                .build();
    }

    /**
     * Informations publiques du lot.
     */
    private PublicTraceabilityResponse.LotInfo buildLotInfo(
            LotProduitFini lot
    ) {

        return PublicTraceabilityResponse.LotInfo.builder()
                .id(lot.getId())
                .numeroLot(lot.getNumeroLot())
                .dateExpiration(lot.getDateExpiration())
                .statut(
                        lot.getStatut() != null
                                ? lot.getStatut().name()
                                : null
                )
                .dateProduction(getDateProduction(lot))
                .build();
    }

    /**
     * Récupère la date réelle de production.
     *
     * On utilise la date de fin de production car le lot
     * est considéré comme produit une fois la production terminée.
     */
    private java.time.LocalDateTime getDateProduction(
            LotProduitFini lot
    ) {

        if (lot.getProduction() == null) {
            return null;
        }

        return lot.getProduction().getDateFin();
    }

    /**
     * Construit la liste publique des matières premières utilisées.
     *
     * On ne retourne volontairement pas :
     * - le fournisseur
     * - le numéro du lot MP
     * - la quantité exacte utilisée
     */
    private List<PublicTraceabilityResponse.MatierePremiereInfo>
    buildMatieresPremieres(LotProduitFini lot) {

        if (lot.getProduction() == null
                || lot.getProduction().getMouvementsStock() == null) {
            return List.of();
        }

        List<PublicTraceabilityResponse.MatierePremiereInfo> result =
                new ArrayList<>();

        for (MovementStock movement :
                lot.getProduction().getMouvementsStock()) {

            if (movement == null
                    || movement.getLotMP() == null
                    || movement.getLotMP().getMatierePremiere() == null) {
                continue;
            }

            var matierePremiere =
                    movement.getLotMP().getMatierePremiere();

            result.add(
                    PublicTraceabilityResponse.MatierePremiereInfo.builder()
                            .id(matierePremiere.getId())
                            .code(matierePremiere.getCode())
                            .nom(matierePremiere.getNom())
                            .uniteMesure(
                                    matierePremiere.getUniteMesure() != null
                                            ? matierePremiere
                                            .getUniteMesure()
                                            .name()
                                            : null
                            )
                            .build()
            );
        }

        return result;
    }

    /**
     * Construit l'historique public des étapes de traçabilité.
     */
    private List<PublicTraceabilityResponse.EtapeTraceabiliteInfo>
    buildEtapes(LotProduitFini lot) {

        List<TraceabilityEvent> events =
                traceabilityEventRepository
                        .findByLotProduitFiniIdOrderByEventDateAsc(
                                lot.getId()
                        );

        if (events.isEmpty()) {
            return List.of();
        }

        List<PublicTraceabilityResponse.EtapeTraceabiliteInfo> result =
                new ArrayList<>();

        for (TraceabilityEvent event : events) {

            if (event == null || event.getEventType() == null) {
                continue;
            }

            TypeEvenementTraceabilite type =
                    event.getEventType();

            // Certains événements sont purement internes
            // et ne doivent pas apparaître sur la page publique.
            if (!isPublicEvent(type)) {
                continue;
            }

            result.add(
                    PublicTraceabilityResponse.EtapeTraceabiliteInfo.builder()
                            .type(type.name())
                            .titre(getPublicEventTitle(type))
                            .date(event.getEventDate())
                            .statut("TERMINEE")
                            .build()
            );
        }

        return result;
    }

    /**
     * Définit les événements visibles publiquement.
     */
    private boolean isPublicEvent(
            TypeEvenementTraceabilite type
    ) {

        return switch (type) {

            case DEBUT_PRODUCTION,
                 FIN_PRODUCTION,
                 CONTROLE_QUALITE,
                 DEBUT_AFFINAGE,
                 FIN_AFFINAGE,
                 LIVRAISON -> true;

            default -> false;
        };
    }

    /**
     * Transforme le type technique de l'événement
     * en titre compréhensible par le consommateur.
     */
    private String getPublicEventTitle(
            TypeEvenementTraceabilite type
    ) {

        return switch (type) {

            case DEBUT_PRODUCTION ->
                    "Début de la production";

            case FIN_PRODUCTION ->
                    "Fin de la production";

            case CONTROLE_QUALITE ->
                    "Contrôle qualité";

            case DEBUT_AFFINAGE ->
                    "Début de l'affinage";

            case FIN_AFFINAGE ->
                    "Fin de l'affinage";


            case LIVRAISON ->
                    "Livraison";

            default ->
                    "Étape de production";
        };
    }
}