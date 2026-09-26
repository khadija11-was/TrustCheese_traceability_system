package ma.trustCheese.TrustCheesebackend.controller.referentiels;



import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.entity.Cuve;
import ma.trustCheese.TrustCheesebackend.enums.StatutOperationnel;
import ma.trustCheese.TrustCheesebackend.service.referentiels.CuveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuves")
@RequiredArgsConstructor
public class CuveController {

    private final CuveService cuveService;

    /**
     * Créer une cuve.
     */
    @PostMapping
    public ResponseEntity<Cuve> createCuve(
            @RequestBody Cuve cuve
    ) {

        Cuve createdCuve = cuveService.createCuve(cuve);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdCuve);
    }

    /**
     * Récupérer toutes les cuves.
     */
    @GetMapping
    public ResponseEntity<List<Cuve>> getAllCuves() {

        return ResponseEntity.ok(
                cuveService.getAllCuves()
        );
    }

    /**
     * Récupérer une cuve par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Cuve> getCuveById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                cuveService.getCuveById(id)
        );
    }

    /**
     * Récupérer uniquement les cuves disponibles.
     *
     * Utile pour l'écran de démarrage d'une production.
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<Cuve>> getCuvesDisponibles() {

        return ResponseEntity.ok(
                cuveService.getCuvesDisponibles()
        );
    }

    /**
     * Modifier une cuve.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Cuve> updateCuve(
            @PathVariable Long id,
            @RequestBody Cuve cuve
    ) {

        return ResponseEntity.ok(
                cuveService.updateCuve(id, cuve)
        );
    }

    /**
     * Modifier uniquement le statut d'une cuve.
     */
    @PatchMapping("/{id}/statut")
    public ResponseEntity<Cuve> updateStatut(
            @PathVariable Long id,
            @RequestParam StatutOperationnel statut
    ) {

        return ResponseEntity.ok(
                cuveService.updateStatut(id, statut)
        );
    }

    /**
     * Supprimer une cuve.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCuve(
            @PathVariable Long id
    ) {

        cuveService.deleteCuve(id);

        return ResponseEntity.noContent().build();
    }
}
