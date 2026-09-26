package ma.trustCheese.TrustCheesebackend.controller;



import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.AffinageRequest;
import ma.trustCheese.TrustCheesebackend.dto.AffinageResponse;
import ma.trustCheese.TrustCheesebackend.enums.StatutAffinage;
import ma.trustCheese.TrustCheesebackend.service.AffinageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/affinages")
@RequiredArgsConstructor
public class AffinageController {

    private final AffinageService affinageService;


    /**
     * Créer et démarrer un affinage.
     *
     * Le backend :
     * - vérifie la production
     * - récupère tous ses lots disponibles
     * - crée l'affinage
     * - affecte les lots à l'affinage
     * - passe les lots à EN_AFFINAGE
     */
    @PostMapping
    public ResponseEntity<AffinageResponse> createAffinage(
            @Valid @RequestBody AffinageRequest request) {

        AffinageResponse response =
                affinageService.createAffinage(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    /**
     * Terminer un affinage.
     *
     * Attention :
     * les lots restent EN_AFFINAGE.
     * Leur statut changera uniquement lors du passage
     * vers l'étape suivante.
     */
    @PatchMapping("/{id}/terminer")
    public ResponseEntity<AffinageResponse> terminerAffinage(
            @PathVariable Long id) {

        AffinageResponse response =
                affinageService.terminerAffinage(id);

        return ResponseEntity.ok(response);
    }


    /**
     * Annuler un affinage.
     *
     * Les lots associés redeviennent DISPONIBLE.
     */
    @PatchMapping("/{id}/annuler")
    public ResponseEntity<AffinageResponse> annulerAffinage(
            @PathVariable Long id) {

        AffinageResponse response =
                affinageService.annulerAffinage(id);

        return ResponseEntity.ok(response);
    }


    /**
     * Récupérer tous les affinages.
     *
     * Exemple :
     * GET /api/affinages
     */
    @GetMapping
    public ResponseEntity<List<AffinageResponse>> getAllAffinages() {

        List<AffinageResponse> affinages =
                affinageService.getAllAffinages();

        return ResponseEntity.ok(affinages);
    }


    /**
     * Récupérer un affinage par son identifiant.
     *
     * Exemple :
     * GET /api/affinages/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<AffinageResponse> getAffinageById(
            @PathVariable Long id) {

        AffinageResponse response =
                affinageService.getAffinageById(id);

        return ResponseEntity.ok(response);
    }


    /**
     * Récupérer les affinages par statut.
     *
     * Exemple :
     * GET /api/affinages?statut=EN_COURS
     */
    @GetMapping(params = "statut")
    public ResponseEntity<List<AffinageResponse>> getAffinagesByStatut(
            @RequestParam StatutAffinage statut) {

        List<AffinageResponse> affinages =
                affinageService.getAffinagesByStatut(statut);

        return ResponseEntity.ok(affinages);
    }
}
