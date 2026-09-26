package ma.trustCheese.TrustCheesebackend.controller.stock;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.MovementStockRequest;
import ma.trustCheese.TrustCheesebackend.dto.MovementStockResponse;
import ma.trustCheese.TrustCheesebackend.service.stock.MovementStockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mouvements-stock")
@RequiredArgsConstructor
public class MovementStockController {

    private final MovementStockService movementStockService;

    @PostMapping
    public ResponseEntity<MovementStockResponse> create(
            @Valid @RequestBody MovementStockRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(movementStockService.create(request));
    }


    @GetMapping
    public ResponseEntity<List<MovementStockResponse>> getAll() {

        return ResponseEntity.ok(
                movementStockService.getAll()
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<MovementStockResponse> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                movementStockService.getById(id)
        );
    }


    @GetMapping("/lot/{lotMPId}")
    public ResponseEntity<List<MovementStockResponse>> getByLotMP(
            @PathVariable Long lotMPId
    ) {

        return ResponseEntity.ok(
                movementStockService.getByLotMP(lotMPId)
        );
    }


    @GetMapping("/production/{productionId}")
    public ResponseEntity<List<MovementStockResponse>> getByProduction(
            @PathVariable Long productionId
    ) {

        return ResponseEntity.ok(
                movementStockService.getByProduction(productionId)
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<MovementStockResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody MovementStockRequest request
    ) {

        return ResponseEntity.ok(
                movementStockService.update(id, request)
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        movementStockService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
