package ma.trustCheese.TrustCheesebackend.service.traceability;

import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.traceability.TraceabilityResponse;
import ma.trustCheese.TrustCheesebackend.entity.*;
import ma.trustCheese.TrustCheesebackend.enums.TypeEvenementTraceabilite;
import ma.trustCheese.TrustCheesebackend.repository.LotProduitFiniRepository;
import ma.trustCheese.TrustCheesebackend.repository.MovementStockRepository;
import ma.trustCheese.TrustCheesebackend.repository.TraceabilityEventRepository;
import ma.trustCheese.TrustCheesebackend.repository.UtilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TraceabilityEventService {

    private final TraceabilityEventRepository traceabilityEventRepository;

    private final LotProduitFiniRepository lotProduitFiniRepository;

    private final UtilisateurRepository utilisateurRepository;

    private final ObjectMapper objectMapper;

    private final MovementStockRepository movementStockRepository;


    // =========================================================
    // CREATION D'UN EVENEMENT DE TRACEABILITE
    // =========================================================

    /**
     * Crée un événement de traçabilité pour un lot de produit fini.
     */
    public TraceabilityEvent createEvent(
            Long lotProduitFiniId,
            TypeEvenementTraceabilite eventType,
            JsonNode payload
    ) {

        LotProduitFini lotProduitFini =
                lotProduitFiniRepository
                        .findById(lotProduitFiniId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lot produit fini introuvable avec l'id : "
                                                + lotProduitFiniId
                                )
                        );

        Utilisateur operator = getAuthenticatedUser();

        TraceabilityEvent event = TraceabilityEvent.builder()
                .eventType(eventType)
                .lotProduitFini(lotProduitFini)
                .eventDate(java.time.LocalDateTime.now())
                .operator(operator)
                .payload(payload)
                .build();

        return traceabilityEventRepository.save(event);
    }


    // =========================================================
    // ANCIENNE TIMELINE
    // =========================================================

    /**
     * Récupère tous les événements d'un lot
     * dans l'ordre chronologique.
     */
    @Transactional(readOnly = true)
    public List<TraceabilityEvent> getLotTimeline(
            Long lotProduitFiniId
    ) {

        if (!lotProduitFiniRepository.existsById(lotProduitFiniId)) {
            throw new RuntimeException(
                    "Lot produit fini introuvable avec l'id : "
                            + lotProduitFiniId
            );
        }

        return traceabilityEventRepository
                .findByLotProduitFiniIdOrderByEventDateAsc(
                        lotProduitFiniId
                );
    }


    // =========================================================
    // NOUVELLE TRACEABILITE COMPLETE
    // =========================================================

    /**
     * Récupère la traçabilité complète d'un lot :
     *
     * - informations du lot
     * - matières premières utilisées
     * - événements de traçabilité
     *
     * La production n'est pas retournée séparément car
     * ses informations historiques sont déjà présentes
     * dans les événements DEBUT_PRODUCTION et FIN_PRODUCTION.
     */
    @Transactional(readOnly = true)
    public TraceabilityResponse getTraceability(
            Long lotProduitFiniId
    ) {

        if (lotProduitFiniId == null) {
            throw new IllegalArgumentException(
                    "L'identifiant du lot est obligatoire."
            );
        }

        LotProduitFini lot =
                lotProduitFiniRepository
                        .findById(lotProduitFiniId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lot produit fini introuvable avec l'id : "
                                                + lotProduitFiniId
                                )
                        );

        return TraceabilityResponse.builder()

                // Informations du lot
                .lot(mapLot(lot))

                // Matières premières consommées
                .matieresPremieres(
                        mapMatieresPremieres(lot)
                )

                // Timeline des événements
                .events(
                        mapEvents(lotProduitFiniId)
                )

                .build();
    }

        @Transactional(readOnly = true)
        public TraceabilityResponse getTraceabilityByNumeroLot(String numeroLot) {
                LotProduitFini lot = lotProduitFiniRepository.findByNumeroLot(numeroLot)
                                .orElseThrow(() -> new RuntimeException("Lot produit fini introuvable : " + numeroLot));
                return getTraceability(lot.getId());
        }

        @Transactional(readOnly = true)
        public List<TraceabilityResponse.EventInfo> getRecentEvents() {
                return traceabilityEventRepository.findTop5ByOrderByEventDateDesc()
                                .stream()
                                .map(this::mapEvent)
                                .toList();
        }


    // =========================================================
    // MAPPING DU LOT
    // =========================================================

    private TraceabilityResponse.LotInfo mapLot(
            LotProduitFini lot
    ) {

        return TraceabilityResponse.LotInfo.builder()

                .id(lot.getId())

                .numeroLot(lot.getNumeroLot())

                .dateExpiration(lot.getDateExpiration())

                .statut(
                        lot.getStatut() != null
                                ? lot.getStatut().name()
                                : null
                )

                .build();
    }


    // =========================================================
    // MATIERES PREMIERES
    // =========================================================

    /**
     * Récupère les matières premières consommées
     * pour la production du lot.
     *
     * Chemin :
     *
     * LotProduitFini
     *      ↓
     * Production
     *      ↓
     * MovementStock
     *      ↓
     * LotMP
     *      ↓
     * MatierePremiere + Fournisseur
     */
    private List<TraceabilityResponse.MatierePremiereInfo>
    mapMatieresPremieres(LotProduitFini lot) {

        Production production = lot.getProduction();

        if (production == null || production.getId() == null) {
            return Collections.emptyList();
        }

        List<MovementStock> mouvements =
                movementStockRepository
                        .findByProductionIdOrderByDateMouvementAsc(
                                production.getId()
                        );

        if (mouvements == null || mouvements.isEmpty()) {
            return Collections.emptyList();
        }

        return mouvements.stream()
                .map(this::mapMatierePremiere)
                .filter(java.util.Objects::nonNull)
                .toList();
    }


    // =========================================================
    // MAPPING D'UNE MATIERE PREMIERE
    // =========================================================

    private TraceabilityResponse.MatierePremiereInfo
    mapMatierePremiere(MovementStock movement) {

        if (movement == null) {
            return null;
        }

        LotMP lotMP = movement.getLotMP();

        if (lotMP == null) {
            return null;
        }

        MatierePremiere matierePremiere =
                lotMP.getMatierePremiere();

        Fournisseur fournisseur =
                lotMP.getFournisseur();

        return TraceabilityResponse.MatierePremiereInfo.builder()

                // -------------------------------------------------
                // LOT DE MATIERE PREMIERE
                // -------------------------------------------------

                .lotMPId(
                        lotMP.getId()
                )

                .numeroLotMP(
                        lotMP.getNumeroLot()
                )


                // -------------------------------------------------
                // MATIERE PREMIERE
                // -------------------------------------------------

                .matierePremiereId(
                        matierePremiere != null
                                ? matierePremiere.getId()
                                : null
                )

                .codeMatierePremiere(
                        matierePremiere != null
                                ? matierePremiere.getCode()
                                : null
                )

                .nomMatierePremiere(
                        matierePremiere != null
                                ? matierePremiere.getNom()
                                : null
                )


                // -------------------------------------------------
                // QUANTITE CONSOMMEE
                // -------------------------------------------------
                //
                // IMPORTANT :
                //
                // lotMP.getQuantite()
                // = quantité reçue
                //
                // movement.getQuantite()
                // = quantité consommée
                //
                // Pour la traçabilité, on retourne donc
                // movement.getQuantite().
                // -------------------------------------------------

                .quantiteUtilisee(
                        movement.getQuantite()
                )


                // -------------------------------------------------
                // UNITE DE MESURE
                // -------------------------------------------------

                .uniteMesure(
                        matierePremiere != null
                                && matierePremiere.getUniteMesure() != null
                                ? matierePremiere
                                .getUniteMesure()
                                .name()
                                : null
                )


                // -------------------------------------------------
                // FOURNISSEUR
                // -------------------------------------------------

                .fournisseurId(
                        fournisseur != null
                                ? fournisseur.getId()
                                : null
                )

                .fournisseurNom(
                        fournisseur != null
                                ? fournisseur.getNom()
                                : null
                )


                // -------------------------------------------------
                // DATE DE RECEPTION
                // -------------------------------------------------

                .dateReception(
                        lotMP.getDateReception()
                )

                .build();
    }


    // =========================================================
    // EVENEMENTS
    // =========================================================

    private List<TraceabilityResponse.EventInfo>
    mapEvents(Long lotProduitFiniId) {

        List<TraceabilityEvent> events =
                traceabilityEventRepository
                        .findByLotProduitFiniIdOrderByEventDateAsc(
                                lotProduitFiniId
                        );

        if (events == null || events.isEmpty()) {
            return Collections.emptyList();
        }

        return events.stream()
                .map(this::mapEvent)
                .toList();
    }


    // =========================================================
    // MAPPING D'UN EVENEMENT
    // =========================================================

    private TraceabilityResponse.EventInfo mapEvent(
            TraceabilityEvent event
    ) {

        return TraceabilityResponse.EventInfo.builder()

                .eventId(
                        event.getEventId()
                )

                .eventType(
                        event.getEventType() != null
                                ? event.getEventType().name()
                                : null
                )

                .eventDate(
                        event.getEventDate()
                )

                .operatorNom(
                        event.getOperator() != null
                                ? event.getOperator().getNom()
                                : null
                )

                .payload(
                        event.getPayload()
                )

                .build();
    }


    // =========================================================
    // UTILISATEUR AUTHENTIFIE
    // =========================================================

    /**
     * Récupère l'utilisateur actuellement authentifié.
     */
    private Utilisateur getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(
                authentication.getPrincipal()
        )) {

            throw new RuntimeException(
                    "Aucun utilisateur authentifié."
            );
        }

        String email = authentication.getName();

        return utilisateurRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Utilisateur connecté introuvable."
                        )
                );
    }


    // =========================================================
    // EVENEMENTS DE PRODUCTION
    // =========================================================

    public void createProductionEvents(
            List<LotProduitFini> lots,
            Production production
    ) {

        if (lots == null || lots.isEmpty()) {
            throw new IllegalArgumentException(
                    "La liste des lots produits ne peut pas être vide."
            );
        }

        if (production == null) {
            throw new IllegalArgumentException(
                    "La production est obligatoire."
            );
        }

        if (production.getDateDebut() == null) {
            throw new IllegalArgumentException(
                    "La date de début réelle de la production est obligatoire."
            );
        }

        if (production.getDateFin() == null) {
            throw new IllegalArgumentException(
                    "La date de fin réelle de la production est obligatoire."
            );
        }

        if (production.getOperateur() == null) {
            throw new IllegalArgumentException(
                    "L'opérateur de la production est obligatoire."
            );
        }

        List<TraceabilityEvent> events = new ArrayList<>();

        for (LotProduitFini lot : lots) {

            if (lot == null || lot.getId() == null) {
                throw new IllegalArgumentException(
                        "Chaque lot produit doit être enregistré avant "
                                + "la création des événements."
                );
            }

            ObjectNode startPayload =
                    objectMapper.createObjectNode();

            startPayload.put(
                    "numeroProduction",
                    production.getNumeroProduction()
            );

            if (production.getCuve() != null) {
                startPayload.put(
                        "cuve",
                        production.getCuve().getNom()
                );
            }

            if (production.getTemperatureCuve() != null) {
                startPayload.put(
                        "temperatureCuve",
                        production.getTemperatureCuve().doubleValue()
                );
            }

            if (production.getPhCuve() != null) {
                startPayload.put(
                        "phCuve",
                        production.getPhCuve().doubleValue()
                );
            }

            // Début de production
            TraceabilityEvent startEvent =
                    TraceabilityEvent.builder()
                            .eventType(
                                    TypeEvenementTraceabilite.DEBUT_PRODUCTION
                            )
                            .lotProduitFini(lot)
                            .eventDate(production.getDateDebut())
                            .operator(production.getOperateur())
                            .payload(startPayload)
                            .build();

            events.add(startEvent);


            ObjectNode endPayload =
                    objectMapper.createObjectNode();

            endPayload.put(
                    "numeroProduction",
                    production.getNumeroProduction()
            );

            // Fin de production
            TraceabilityEvent endEvent =
                    TraceabilityEvent.builder()
                            .eventType(
                                    TypeEvenementTraceabilite.FIN_PRODUCTION
                            )
                            .lotProduitFini(lot)
                            .eventDate(production.getDateFin())
                            .operator(production.getOperateur())
                            .payload(endPayload)
                            .build();

            events.add(endEvent);
        }

        traceabilityEventRepository.saveAll(events);
    }


    // =========================================================
    // DEBUT AFFINAGE
    // =========================================================

    public void createAffinageStartEvents(
            List<LotProduitFini> lots,
            Affinage affinage
    ) {

        if (lots == null || lots.isEmpty()) {
            throw new IllegalArgumentException(
                    "La liste des lots ne peut pas être vide."
            );
        }

        if (affinage == null) {
            throw new IllegalArgumentException(
                    "L'affinage est obligatoire."
            );
        }

        if (affinage.getDateDebutReelle() == null) {
            throw new IllegalArgumentException(
                    "La date de début réelle de l'affinage est obligatoire."
            );
        }

        Utilisateur operator = getAuthenticatedUser();

        List<TraceabilityEvent> events = new ArrayList<>();

        for (LotProduitFini lot : lots) {

            if (lot == null || lot.getId() == null) {
                throw new IllegalArgumentException(
                        "Chaque lot doit être enregistré avant "
                                + "la création de l'événement."
                );
            }

            TraceabilityEvent event =
                    TraceabilityEvent.builder()
                            .eventType(
                                    TypeEvenementTraceabilite.DEBUT_AFFINAGE
                            )
                            .lotProduitFini(lot)
                            .eventDate(affinage.getDateDebutReelle())
                            .operator(operator)
                            .build();

            events.add(event);
        }

        traceabilityEventRepository.saveAll(events);
    }


    // =========================================================
    // FIN AFFINAGE
    // =========================================================

    public void createAffinageEndEvents(
            List<LotProduitFini> lots,
            Affinage affinage
    ) {

        if (lots == null || lots.isEmpty()) {
            throw new IllegalArgumentException(
                    "La liste des lots ne peut pas être vide."
            );
        }

        if (affinage == null) {
            throw new IllegalArgumentException(
                    "L'affinage est obligatoire."
            );
        }

        if (affinage.getDateFinReelle() == null) {
            throw new IllegalArgumentException(
                    "La date de fin réelle de l'affinage est obligatoire."
            );
        }

        Utilisateur operator = getAuthenticatedUser();

        List<TraceabilityEvent> events = new ArrayList<>();

        for (LotProduitFini lot : lots) {

            if (lot == null || lot.getId() == null) {
                throw new IllegalArgumentException(
                        "Chaque lot doit être enregistré avant "
                                + "la création de l'événement."
                );
            }

            ObjectNode payload =
                    objectMapper.createObjectNode();

            payload.put(
                    "affinageId",
                    affinage.getId()
            );

            payload.put(
                    "numeroAffinage",
                    affinage.getNumeroAffinage()
            );

            if (affinage.getTemperature() != null) {
                payload.put(
                        "temperature",
                        affinage.getTemperature().doubleValue()
                );
            }

            if (affinage.getHumidite() != null) {
                payload.put(
                        "humidite",
                        affinage.getHumidite().doubleValue()
                );
            }

            if (affinage.getObservations() != null) {
                payload.put(
                        "observations",
                        affinage.getObservations()
                );
            }

            TraceabilityEvent event =
                    TraceabilityEvent.builder()
                            .eventType(
                                    TypeEvenementTraceabilite.FIN_AFFINAGE
                            )
                            .lotProduitFini(lot)
                            .eventDate(affinage.getDateFinReelle())
                            .operator(operator)
                            .payload(payload)
                            .build();

            events.add(event);
        }

        traceabilityEventRepository.saveAll(events);
    }


    // =========================================================
    // CONTROLE QUALITE
    // =========================================================

    public void createQualityControlEvents(
            List<ControleQualite> controles
    ) {

        if (controles == null || controles.isEmpty()) {
            throw new IllegalArgumentException(
                    "La liste des contrôles qualité ne peut pas être vide."
            );
        }

        List<TraceabilityEvent> events = new ArrayList<>();

        for (ControleQualite controle : controles) {

            if (controle == null || controle.getId() == null) {
                throw new IllegalArgumentException(
                        "Chaque contrôle qualité doit être enregistré "
                                + "avant la création de l'événement."
                );
            }

            if (controle.getLotProduitFini() == null) {
                throw new IllegalArgumentException(
                        "Le lot produit fini du contrôle qualité est obligatoire."
                );
            }

            if (controle.getUtilisateur() == null) {
                throw new IllegalArgumentException(
                        "L'utilisateur du contrôle qualité est obligatoire."
                );
            }

            ObjectNode payload =
                    objectMapper.createObjectNode();

            payload.put(
                    "controleQualiteId",
                    controle.getId()
            );

            payload.put(
                    "numeroControle",
                    controle.getNumeroControle()
            );

            if (controle.getTemperature() != null) {
                payload.put(
                        "temperature",
                        controle.getTemperature().doubleValue()
                );
            }

            if (controle.getPh() != null) {
                payload.put(
                        "ph",
                        controle.getPh().doubleValue()
                );
            }

            if (controle.getExtraitSec() != null) {
                payload.put(
                        "extraitSec",
                        controle.getExtraitSec().doubleValue()
                );
            }

            if (controle.getTexture() != null) {
                payload.put(
                        "texture",
                        controle.getTexture()
                );
            }

            if (controle.getNotes() != null) {
                payload.put(
                        "notes",
                        controle.getNotes()
                );
            }

            if (controle.getDecision() != null) {
                payload.put(
                        "decision",
                        controle.getDecision().name()
                );
            }

            TraceabilityEvent event =
                    TraceabilityEvent.builder()
                            .eventType(
                                    TypeEvenementTraceabilite.CONTROLE_QUALITE
                            )
                            .lotProduitFini(
                                    controle.getLotProduitFini()
                            )
                            .eventDate(
                                    controle.getDateControle()
                            )
                            .operator(
                                    controle.getUtilisateur()
                            )
                            .payload(payload)
                            .build();

            events.add(event);
        }

        traceabilityEventRepository.saveAll(events);
    }


    // =========================================================
    // LIVRAISON
    // =========================================================

    public void createDeliveryEvents(
            Livraison livraison
    ) {

        if (livraison == null) {
            throw new IllegalArgumentException(
                    "La livraison est obligatoire."
            );
        }

        if (livraison.getDateLivraisonReelle() == null) {
            throw new IllegalArgumentException(
                    "La date réelle de livraison est obligatoire."
            );
        }

        Utilisateur operator = getAuthenticatedUser();

        List<TraceabilityEvent> events = new ArrayList<>();

        for (LigneLivraison ligne : livraison.getLignes()) {

            if (ligne.getLotsProduitFini() == null
                    || ligne.getLotsProduitFini().isEmpty()) {
                continue;
            }

            for (LotProduitFini lot :
                    ligne.getLotsProduitFini()) {

                ObjectNode payload =
                        objectMapper.createObjectNode();

                payload.put(
                        "livraisonId",
                        livraison.getId()
                );

                payload.put(
                        "numeroLivraison",
                        livraison.getNumeroLivraison()
                );

                // Client
                if (livraison.getClient() != null) {

                    payload.put(
                            "clientId",
                            livraison.getClient().getId()
                    );

                    payload.put(
                            "clientNom",
                            livraison.getClient().getNom()
                    );
                }

                // Produit
                if (ligne.getProduit() != null) {

                    payload.put(
                            "produitId",
                            ligne.getProduit().getId()
                    );

                    payload.put(
                            "produitNom",
                            ligne.getProduit().getNom()
                    );
                }

                // Ligne livraison
                payload.put(
                        "ligneLivraisonId",
                        ligne.getId()
                );

                payload.put(
                        "quantiteLots",
                        ligne.getQuantiteLots()
                );

                if (livraison.getTemperatureMoyenneCamion() != null) {
                    payload.put(
                            "temperatureMoyenneCamion",
                            livraison.getTemperatureMoyenneCamion()
                    );
                }

                // Lot produit fini
                payload.put(
                        "lotId",
                        lot.getId()
                );

                payload.put(
                        "numeroLot",
                        lot.getNumeroLot()
                );

                TraceabilityEvent event =
                        TraceabilityEvent.builder()
                                .eventType(
                                        TypeEvenementTraceabilite.LIVRAISON
                                )
                                .lotProduitFini(lot)
                                .eventDate(
                                        livraison.getDateLivraisonReelle()
                                )
                                .operator(operator)
                                .payload(payload)
                                .build();

                events.add(event);
            }
        }

        if (!events.isEmpty()) {
            traceabilityEventRepository.saveAll(events);
        }
    }
}