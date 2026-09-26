package ma.trustCheese.TrustCheesebackend.controller.stock;



import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.LotMPRequest;
import ma.trustCheese.TrustCheesebackend.dto.LotMPResponse;
import ma.trustCheese.TrustCheesebackend.service.stock.LotMPService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lots-mp")
@RequiredArgsConstructor
public class LotMPController {

    private final LotMPService lotMPService;

    // Créer un lot de matière première
    @PostMapping
    public ResponseEntity<LotMPResponse> create(
            @Valid @RequestBody LotMPRequest request
    ) {
        LotMPResponse response = lotMPService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Récupérer tous les lots
    @GetMapping
    public ResponseEntity<List<LotMPResponse>> getAll() {

        return ResponseEntity.ok(
                lotMPService.getAll()
        );
    }

    // Récupérer un lot par son ID
    @GetMapping("/{id}")
    public ResponseEntity<LotMPResponse> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                lotMPService.getById(id)
        );
    }
}
