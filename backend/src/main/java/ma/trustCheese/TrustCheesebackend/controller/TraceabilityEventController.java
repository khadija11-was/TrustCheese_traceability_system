package ma.trustCheese.TrustCheesebackend.controller;

import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.traceability.TraceabilityResponse;
import ma.trustCheese.TrustCheesebackend.entity.TraceabilityEvent;
import ma.trustCheese.TrustCheesebackend.enums.TypeEvenementTraceabilite;
import ma.trustCheese.TrustCheesebackend.service.traceability.TraceabilityEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;

import java.util.List;

@RestController
@RequestMapping("/api/traceability")
@RequiredArgsConstructor
public class TraceabilityEventController {

    private final TraceabilityEventService traceabilityEventService;

    @GetMapping("/lots/search")
    public ResponseEntity<TraceabilityResponse> searchByNumeroLot(
            @RequestParam String numeroLot
    ) {
        return ResponseEntity.ok(
                traceabilityEventService.getTraceabilityByNumeroLot(numeroLot)
        );
    }

    @GetMapping("/events/recent")
    public ResponseEntity<List<TraceabilityResponse.EventInfo>> getRecentEvents() {
        return ResponseEntity.ok(traceabilityEventService.getRecentEvents());
    }


    // =========================================================
    // TRACEABILITE COMPLETE D'UN LOT
    // =========================================================

    /**
     * Récupère la traçabilité complète d'un lot :
     *
     * - informations du lot
     * - matières premières utilisées
     * - événements de traçabilité
     *
     * Exemple :
     * GET /api/traceability/lots/1
     */
    @GetMapping("/lots/{lotId}")
    public ResponseEntity<TraceabilityResponse> getLotTraceability(
            @PathVariable Long lotId
    ) {

        TraceabilityResponse response =
                traceabilityEventService.getTraceability(lotId);

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // TIMELINE BRUTE DES EVENEMENTS
    // =========================================================

    /**
     * Récupère uniquement les événements de traçabilité
     * d'un lot dans l'ordre chronologique.
     *
     * Cette route peut être utile en interne si on a besoin
     * uniquement de la timeline.
     *
     * Exemple :
     * GET /api/traceability/lots/1/events
     */
    @GetMapping("/lots/{lotId}/events")
    public ResponseEntity<List<TraceabilityEvent>> getLotTimeline(
            @PathVariable Long lotId
    ) {

        List<TraceabilityEvent> events =
                traceabilityEventService.getLotTimeline(lotId);

        return ResponseEntity.ok(events);
    }


    // =========================================================
    // CREATION D'UN EVENEMENT
    // =========================================================

    /**
     * Crée manuellement un événement de traçabilité
     * pour un lot.
     *
     * Exemple :
     * POST /api/traceability/lots/1/events
     */
    @PostMapping("/lots/{lotId}/events")
    public ResponseEntity<TraceabilityEvent> createEvent(
            @PathVariable Long lotId,
            @RequestBody CreateTraceabilityEventRequest request
    ) {

        TraceabilityEvent event =
                traceabilityEventService.createEvent(
                        lotId,
                        request.getEventType(),
                        request.getPayload()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(event);
    }


    // =========================================================
    // DTO DE CREATION D'UN EVENEMENT
    // =========================================================

    public static class CreateTraceabilityEventRequest {

        private TypeEvenementTraceabilite eventType;

        private JsonNode payload;


        public TypeEvenementTraceabilite getEventType() {
            return eventType;
        }

        public void setEventType(
                TypeEvenementTraceabilite eventType
        ) {
            this.eventType = eventType;
        }


        public JsonNode getPayload() {
            return payload;
        }

        public void setPayload(JsonNode payload) {
            this.payload = payload;
        }
    }
}