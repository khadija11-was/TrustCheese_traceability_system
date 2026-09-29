package ma.trustCheese.TrustCheesebackend.controller;


import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.traceability.PublicTraceabilityResponse;
import ma.trustCheese.TrustCheesebackend.service.traceability.PublicTraceabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/traceability")
@RequiredArgsConstructor
public class PublicTraceabilityController {

    private final PublicTraceabilityService publicTraceabilityService;

    /**
     * Récupère les informations publiques de traçabilité
     * d'un lot à partir de son numéro.
     * Exemple :
     * GET /api/public/traceability/lots/LOT-20260927-0001
     */
    @GetMapping("/lots/{numeroLot}")
    public ResponseEntity<PublicTraceabilityResponse> getPublicTraceability(
            @PathVariable String numeroLot
    ) {

        PublicTraceabilityResponse response =
                publicTraceabilityService.getPublicTraceability(numeroLot);

        return ResponseEntity.ok(response);
    }
}
