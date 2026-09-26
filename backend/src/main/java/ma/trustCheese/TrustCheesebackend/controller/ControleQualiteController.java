package ma.trustCheese.TrustCheesebackend.controller;

import ma.trustCheese.TrustCheesebackend.service.ControleQualiteService;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.ControleQualiteRequest;
import ma.trustCheese.TrustCheesebackend.dto.ControleQualiteResponse;
import ma.trustCheese.TrustCheesebackend.enums.StatutAffinage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/controles-qualite")
@RequiredArgsConstructor
public class ControleQualiteController {

    private final ControleQualiteService controleQualiteService;

    @PostMapping
    public ResponseEntity<ControleQualiteResponse> effectuerControleQualite(
            @Valid @RequestBody ControleQualiteRequest request) {

        ControleQualiteResponse response =
                controleQualiteService.effectuerControleQualite(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
        public ResponseEntity<List<ControleQualiteResponse>> getAllControles() {

        return ResponseEntity.ok(
                controleQualiteService.getAllControles()
        );
    }

    @GetMapping("/lot/{lotProduitFiniId}")
        public ResponseEntity<List<ControleQualiteResponse>> getControlesByLot(
            @PathVariable Long lotProduitFiniId) {

        return ResponseEntity.ok(
                controleQualiteService.getControlesByLot(lotProduitFiniId)
        );
    }

    @GetMapping("/utilisateur/{utilisateurId}")
        public ResponseEntity<List<ControleQualiteResponse>> getControlesByUtilisateur(
            @PathVariable Long utilisateurId) {

        return ResponseEntity.ok(
                controleQualiteService.getControlesByUtilisateur(utilisateurId)
        );
    }
}