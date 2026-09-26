package ma.trustCheese.TrustCheesebackend.controller.referentiels;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.FournisseurRequest;
import ma.trustCheese.TrustCheesebackend.dto.FournisseurResponse;
import ma.trustCheese.TrustCheesebackend.service.referentiels.FournisseurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fournisseurs")
@RequiredArgsConstructor
public class FournisseurController {

    private final FournisseurService fournisseurService;

    // GET /api/fournisseurs
    @GetMapping
    //@PreAuthorize("hasAnyRole('OPERATEUR','MANAGER','ADMIN','LOGISTIQUE')")
    public ResponseEntity<List<FournisseurResponse>> getAll() {
        return ResponseEntity.ok(fournisseurService.getAll());
    }

    // GET /api/fournisseurs/{id}
    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyRole('OPERATEUR','MANAGER','ADMIN','LOGISTIQUE')")
    public ResponseEntity<FournisseurResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(fournisseurService.getById(id));
    }

    // GET /api/fournisseurs/code/{code}
    @GetMapping("/code/{code}")
    //@PreAuthorize("hasAnyRole('OPERATEUR','MANAGER','ADMIN','LOGISTIQUE')")
    public ResponseEntity<FournisseurResponse> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(fournisseurService.getByCode(code));
    }

    // GET /api/fournisseurs/search?keyword=xxx
    @GetMapping("/search")
    //@PreAuthorize("hasAnyRole('OPERATEUR','MANAGER','ADMIN','LOGISTIQUE')")
    public ResponseEntity<List<FournisseurResponse>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(fournisseurService.search(keyword));
    }

    // POST /api/fournisseurs
    @PostMapping
    //@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<FournisseurResponse> create(@Valid @RequestBody FournisseurRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fournisseurService.create(request));
    }

    // PUT /api/fournisseurs/{id}
    @PutMapping("/{id}")
    //@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<FournisseurResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FournisseurRequest request
    ) {
        return ResponseEntity.ok(fournisseurService.update(id, request));
    }

    // DELETE /api/fournisseurs/{id}
    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fournisseurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}