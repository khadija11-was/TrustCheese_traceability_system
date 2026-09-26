package ma.trustCheese.TrustCheesebackend.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.ProductionRequest;
import ma.trustCheese.TrustCheesebackend.dto.ProductionResponse;
import ma.trustCheese.TrustCheesebackend.dto.ProductionMesureRequest;
import ma.trustCheese.TrustCheesebackend.service.ProductionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productions")
@RequiredArgsConstructor
public class ProductionController {

    private final ProductionService productionService;


    // ============================================================
    // 1. PLANIFIER UNE PRODUCTION
    // ============================================================

    @PostMapping
    public ResponseEntity<ProductionResponse> planProduction(
            @Valid @RequestBody ProductionRequest request) {

        ProductionResponse response =
                productionService.planProduction(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // ============================================================
    // 2. DÉMARRER UNE PRODUCTION
    // ============================================================

    @PatchMapping("/{id}/start")
    public ResponseEntity<ProductionResponse> startProduction(
            @PathVariable Long id,
            @RequestParam Long cuveId,
            @RequestParam BigDecimal temperatureCuve,
            @RequestParam BigDecimal phCuve) {

        ProductionResponse response =
                productionService.startProduction(id, cuveId, temperatureCuve, phCuve);

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 3. TERMINER UNE PRODUCTION
    // ============================================================

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ProductionResponse> completeProduction(
            @PathVariable Long id,
            @Valid @RequestBody CompleteProductionRequest request) {

        ProductionResponse response =
                productionService.completeProduction(
                        id,
                        request.nombreLotsProduits()
                );

        return ResponseEntity.ok(response);
    }

        @PatchMapping("/{id}/measurements")
        public ResponseEntity<ProductionResponse> updateMeasurements(
                        @PathVariable Long id,
                        @Valid @RequestBody ProductionMesureRequest request) {

                return ResponseEntity.ok(
                                productionService.updateMesures(id, request)
                );
        }


    // ============================================================
    // 4. ANNULER UNE PRODUCTION
    // ============================================================

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ProductionResponse> cancelProduction(
            @PathVariable Long id) {

        ProductionResponse response =
                productionService.cancelProduction(id);

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // 5. RÉCUPÉRER TOUTES LES PRODUCTIONS
    // ============================================================

    @GetMapping
    public ResponseEntity<List<ProductionResponse>> getAllProductions() {

        List<ProductionResponse> productions =
                productionService.getAllProductions();

        return ResponseEntity.ok(productions);
    }


    // ============================================================
    // 6. RÉCUPÉRER UNE PRODUCTION PAR ID
    // ============================================================

    @GetMapping("/{id}")
    public ResponseEntity<ProductionResponse> getProductionById(
            @PathVariable Long id) {

        ProductionResponse response =
                productionService.getProductionById(id);

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // DTO INTERNE POUR LA FINALISATION
    // ============================================================

    public record CompleteProductionRequest(

            @NotNull(message = "Le nombre de lots produits est obligatoire")
            @Positive(message = "Le nombre de lots produits doit être supérieur à 0")
            Integer nombreLotsProduits

    ) {
    }
}

