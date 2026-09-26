package ma.trustCheese.TrustCheesebackend.controller.referentiels;



import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.MatierePremiereRequest;
import ma.trustCheese.TrustCheesebackend.dto.MatierePremiereResponse;
import ma.trustCheese.TrustCheesebackend.enums.UniteMesure;
import ma.trustCheese.TrustCheesebackend.service.referentiels.MatierePremiereService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matieres-premieres")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MatierePremiereController {

    private final MatierePremiereService matierePremiereService;

    // GET /api/matieres-premieres
    @GetMapping
    //@PreAuthorize("hasAnyRole('OPERATEUR','MANAGER','ADMIN','LOGISTIQUE')")
    public ResponseEntity<List<MatierePremiereResponse>> getAll() {
        return ResponseEntity.ok(matierePremiereService.getAll());
    }

    // GET /api/matieres-premieres/{id}
    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyRole('OPERATEUR','MANAGER','ADMIN','LOGISTIQUE')")
    public ResponseEntity<MatierePremiereResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(matierePremiereService.getById(id));
    }

    // GET /api/matieres-premieres/unite?uniteMesure=KG
    @GetMapping("/unite")
    //@PreAuthorize("hasAnyRole('OPERATEUR','MANAGER','ADMIN','LOGISTIQUE')")
    public ResponseEntity<List<MatierePremiereResponse>> getByUnite(
            @RequestParam UniteMesure uniteMesure
    ) {
        return ResponseEntity.ok(matierePremiereService.getByUniteMesure(uniteMesure));
    }

    // GET /api/matieres-premieres/search?keyword=xxx
    @GetMapping("/search")
    //@PreAuthorize("hasAnyRole('OPERATEUR','MANAGER','ADMIN','LOGISTIQUE')")
    public ResponseEntity<List<MatierePremiereResponse>> search(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(matierePremiereService.search(keyword));
    }

    // POST /api/matieres-premieres
    @PostMapping
    //@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<MatierePremiereResponse> create(
            @Valid @RequestBody MatierePremiereRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(matierePremiereService.create(request));
    }

    // PUT /api/matieres-premieres/{id}
    @PutMapping("/{id}")
    //@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<MatierePremiereResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody MatierePremiereRequest request
    ) {
        return ResponseEntity.ok(matierePremiereService.update(id, request));
    }

    // DELETE /api/matieres-premieres/{id}
    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        matierePremiereService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
