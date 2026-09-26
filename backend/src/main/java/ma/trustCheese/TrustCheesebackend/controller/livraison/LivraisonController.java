package ma.trustCheese.TrustCheesebackend.controller.livraison;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.livraison.LivraisonRequest;
import ma.trustCheese.TrustCheesebackend.dto.livraison.LivraisonResponse;
import ma.trustCheese.TrustCheesebackend.enums.StatutLivraison;
import ma.trustCheese.TrustCheesebackend.service.livraison.LivraisonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livraisons")
@RequiredArgsConstructor
public class LivraisonController {

    private final LivraisonService livraisonService;


    // =========================================================
    // CREATE
    // =========================================================

    @PostMapping
    public ResponseEntity<LivraisonResponse> createLivraison(
            @Valid @RequestBody LivraisonRequest request
    ) {

        LivraisonResponse response =
                livraisonService.createLivraison(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @GetMapping
    public ResponseEntity<List<LivraisonResponse>> getAllLivraisons() {

        List<LivraisonResponse> livraisons =
                livraisonService.getAllLivraisons();

        return ResponseEntity.ok(livraisons);
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<LivraisonResponse> getLivraisonById(
            @PathVariable Long id
    ) {

        LivraisonResponse response =
                livraisonService.getLivraisonById(id);

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // GET BY CLIENT
    // =========================================================

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<LivraisonResponse>> getLivraisonsByClient(
            @PathVariable Long clientId
    ) {

        List<LivraisonResponse> livraisons =
                livraisonService.getLivraisonsByClient(clientId);

        return ResponseEntity.ok(livraisons);
    }


    // =========================================================
    // GET BY STATUS
    // =========================================================

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<LivraisonResponse>> getLivraisonsByStatut(
            @PathVariable StatutLivraison statut
    ) {

        List<LivraisonResponse> livraisons =
                livraisonService.getLivraisonsByStatut(statut);

        return ResponseEntity.ok(livraisons);
    }


    // =========================================================
    // PREPARATION
    // =========================================================

    @PostMapping("/{id}/preparer")
    public ResponseEntity<LivraisonResponse> preparerLivraison(
            @PathVariable Long id
    ) {

        LivraisonResponse response =
                livraisonService.preparerLivraison(id);

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // EXPEDITION
    // =========================================================

    @PostMapping("/{id}/expedier")
    public ResponseEntity<LivraisonResponse> expedierLivraison(
            @PathVariable Long id
    ) {

        LivraisonResponse response =
                livraisonService.expedierLivraison(id);

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // CONFIRMATION
    // =========================================================

    @PostMapping("/{id}/confirmer")
    public ResponseEntity<LivraisonResponse> confirmerLivraison(
            @PathVariable Long id
    ) {

        LivraisonResponse response =
                livraisonService.confirmerLivraison(id);

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // ANNULATION
    // =========================================================

    @PostMapping("/{id}/annuler")
    public ResponseEntity<LivraisonResponse> annulerLivraison(
            @PathVariable Long id
    ) {

        LivraisonResponse response =
                livraisonService.annulerLivraison(id);

        return ResponseEntity.ok(response);
    }
}
