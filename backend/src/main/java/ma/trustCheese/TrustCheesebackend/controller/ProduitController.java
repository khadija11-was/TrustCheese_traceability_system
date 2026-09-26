package ma.trustCheese.TrustCheesebackend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.ProduitRequest;
import ma.trustCheese.TrustCheesebackend.dto.ProduitResponse;
import ma.trustCheese.TrustCheesebackend.service.ProduitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    /**
     * Créer un nouveau produit.
     *
     * Requête :
     * POST /api/produits
     *
     * Content-Type :
     * multipart/form-data
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProduitResponse> createProduit(
            @Valid @ModelAttribute ProduitRequest request
    ) {

        ProduitResponse response = produitService.createProduit(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Récupérer tous les produits.
     *
     * GET /api/produits
     */
    @GetMapping
    public ResponseEntity<List<ProduitResponse>> getAllProduits() {

        List<ProduitResponse> produits = produitService.getAllProduits();

        return ResponseEntity.ok(produits);
    }

    /**
     * Récupérer un produit par son ID.
     *
     * GET /api/produits/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProduitResponse> getProduitById(
            @PathVariable Long id
    ) {

        ProduitResponse response = produitService.getProduitById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Modifier un produit.
     *
     * PUT /api/produits/{id}
     *
     * Content-Type :
     * multipart/form-data
     */
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProduitResponse> updateProduit(
            @PathVariable Long id,
            @Valid @ModelAttribute ProduitRequest request
    ) {

        ProduitResponse response =
                produitService.updateProduit(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Supprimer un produit.
     *
     * DELETE /api/produits/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(
            @PathVariable Long id
    ) {

        produitService.deleteProduit(id);

        return ResponseEntity.noContent().build();
    }
}

