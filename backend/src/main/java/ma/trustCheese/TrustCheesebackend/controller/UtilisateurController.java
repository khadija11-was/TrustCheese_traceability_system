package ma.trustCheese.TrustCheesebackend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.UtilisateurRequest;
import ma.trustCheese.TrustCheesebackend.dto.UtilisateurResponse;
import ma.trustCheese.TrustCheesebackend.enums.Role;
import ma.trustCheese.TrustCheesebackend.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    /**
     * Créer un nouvel utilisateur.
     */
    @PostMapping
    public ResponseEntity<UtilisateurResponse> createUtilisateur(
            @Valid @RequestBody UtilisateurRequest request) {

        UtilisateurResponse response =
                utilisateurService.createUtilisateur(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Récupérer tous les utilisateurs.
     */
    @GetMapping
    public ResponseEntity<List<UtilisateurResponse>> getAllUtilisateurs() {

        return ResponseEntity.ok(
                utilisateurService.getAllUtilisateurs()
        );
    }

    /**
     * Récupérer un utilisateur par son identifiant.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurResponse> getUtilisateurById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                utilisateurService.getUtilisateurById(id)
        );
    }

    /**
     * Modifier un utilisateur.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurResponse> updateUtilisateur(
            @PathVariable Long id,
            @Valid @RequestBody UtilisateurRequest request) {

        return ResponseEntity.ok(
                utilisateurService.updateUtilisateur(id, request)
        );
    }

    /**
     * Activer un utilisateur.
     */
    @PatchMapping("/{id}/activer")
    public ResponseEntity<UtilisateurResponse> activerUtilisateur(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                utilisateurService.activerUtilisateur(id)
        );
    }

    /**
     * Désactiver un utilisateur.
     */
    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<UtilisateurResponse> desactiverUtilisateur(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                utilisateurService.desactiverUtilisateur(id)
        );
    }

    /**
     * Rechercher des utilisateurs par nom.
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<UtilisateurResponse>> rechercherParNom(
            @RequestParam String nom) {

        return ResponseEntity.ok(
                utilisateurService.rechercherParNom(nom)
        );
    }

    /**
     * Filtrer les utilisateurs par rôle.
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UtilisateurResponse>> getUtilisateursByRole(
            @PathVariable Role role) {

        return ResponseEntity.ok(
                utilisateurService.getUtilisateursByRole(role)
        );
    }
}
