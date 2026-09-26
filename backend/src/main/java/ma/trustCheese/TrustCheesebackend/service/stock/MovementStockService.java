package ma.trustCheese.TrustCheesebackend.service.stock;


import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.MovementStockRequest;
import ma.trustCheese.TrustCheesebackend.dto.MovementStockResponse;
import ma.trustCheese.TrustCheesebackend.entity.LotMP;
import ma.trustCheese.TrustCheesebackend.entity.MovementStock;
import ma.trustCheese.TrustCheesebackend.entity.Production;
import ma.trustCheese.TrustCheesebackend.repository.LotMPRepository;
import ma.trustCheese.TrustCheesebackend.repository.MovementStockRepository;
import ma.trustCheese.TrustCheesebackend.repository.ProductionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

//@Service
@RequiredArgsConstructor
@Transactional
@Service
public class MovementStockService {

    private final MovementStockRepository movementStockRepository;
    private final LotMPRepository lotMPRepository;
    private final ProductionRepository productionRepository;


    // =========================================================
    // CREATE
    // =========================================================

    public MovementStockResponse create(MovementStockRequest request) {

        LotMP lotMP = lotMPRepository.findById(request.getLotMPId())
                .orElseThrow(() ->
                        new RuntimeException("Lot MP introuvable")
                );

        Production production = productionRepository.findById(
                request.getProductionId()
        ).orElseThrow(() ->
                new RuntimeException("Production introuvable")
        );

        // Quantité déjà consommée dans ce lot
        BigDecimal quantiteConsommee =
                movementStockRepository.sumQuantiteByLotMP(lotMP.getId());

        // Quantité restante avant le nouveau mouvement
        BigDecimal quantiteRestante =
                lotMP.getQuantite()
                        .subtract(quantiteConsommee);

        // Vérification du stock disponible
        verifierQuantiteDisponible(
                request.getQuantite(),
                quantiteRestante
        );

        MovementStock movementStock = MovementStock.builder()
                .quantite(request.getQuantite())
                .dateMouvement(request.getDateMouvement())
                .lotMP(lotMP)
                .production(production)
                .build();

        movementStock = movementStockRepository.save(movementStock);

        BigDecimal nouvelleQuantiteRestante =
                quantiteRestante.subtract(
                        request.getQuantite()
                );

        return toResponse(
                movementStock,
                nouvelleQuantiteRestante
        );
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public MovementStockResponse getById(Long id) {

        MovementStock movementStock =
                movementStockRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Mouvement de stock introuvable"
                                )
                        );

        BigDecimal quantiteRestante =
                calculerQuantiteRestante(
                        movementStock.getLotMP()
                );

        return toResponse(
                movementStock,
                quantiteRestante
        );
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @Transactional(readOnly = true)
    public List<MovementStockResponse> getAll() {

        return movementStockRepository.findAll()
                .stream()
                .map(movementStock -> {

                    BigDecimal quantiteRestante =
                            calculerQuantiteRestante(
                                    movementStock.getLotMP()
                            );

                    return toResponse(
                            movementStock,
                            quantiteRestante
                    );
                })
                .toList();
    }


    // =========================================================
    // GET BY LOT MP
    // =========================================================

    @Transactional(readOnly = true)
    public List<MovementStockResponse> getByLotMP(
            Long lotMPId
    ) {

        // Vérifier que le lot existe
        LotMP lotMP = lotMPRepository.findById(lotMPId)
                .orElseThrow(() ->
                        new RuntimeException("Lot MP introuvable")
                );

        BigDecimal quantiteRestante =
                calculerQuantiteRestante(lotMP);

        return movementStockRepository
                .findByLotMPId(lotMPId)
                .stream()
                .map(movementStock ->
                        toResponse(
                                movementStock,
                                quantiteRestante
                        )
                )
                .toList();
    }


    // =========================================================
    // GET BY PRODUCTION
    // =========================================================

    @Transactional(readOnly = true)
    public List<MovementStockResponse> getByProduction(
            Long productionId
    ) {

        // Vérifier que la production existe
        productionRepository.findById(productionId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Production introuvable"
                        )
                );

        return movementStockRepository
                .findByProductionId(productionId)
                .stream()
                .map(movementStock -> {

                    BigDecimal quantiteRestante =
                            calculerQuantiteRestante(
                                    movementStock.getLotMP()
                            );

                    return toResponse(
                            movementStock,
                            quantiteRestante
                    );
                })
                .toList();
    }


    // =========================================================
    // UPDATE
    // =========================================================

    public MovementStockResponse update(
            Long id,
            MovementStockRequest request
    ) {

        MovementStock movementStock =
                movementStockRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Mouvement de stock introuvable"
                                )
                        );

        LotMP nouveauLotMP =
                lotMPRepository.findById(request.getLotMPId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lot MP introuvable"
                                )
                        );

        Production production =
                productionRepository.findById(
                        request.getProductionId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Production introuvable"
                        )
                );


        /*
         * On calcule la quantité consommée par les AUTRES
         * mouvements du lot.
         */
        BigDecimal quantiteConsommeeAutresMouvements =
                movementStockRepository
                        .sumQuantiteByLotMPExceptMovement(
                                nouveauLotMP.getId(),
                                id
                        );


        BigDecimal quantiteDisponible =
                nouveauLotMP.getQuantite()
                        .subtract(
                                quantiteConsommeeAutresMouvements
                        );


        verifierQuantiteDisponible(
                request.getQuantite(),
                quantiteDisponible
        );


        movementStock.setQuantite(
                request.getQuantite()
        );

        movementStock.setDateMouvement(
                request.getDateMouvement()
        );

        movementStock.setLotMP(
                nouveauLotMP
        );

        movementStock.setProduction(
                production
        );

        movementStock =
                movementStockRepository.save(
                        movementStock
                );


        BigDecimal nouvelleQuantiteRestante =
                calculerQuantiteRestante(
                        nouveauLotMP
                );


        return toResponse(
                movementStock,
                nouvelleQuantiteRestante
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    public void delete(Long id) {

        MovementStock movementStock =
                movementStockRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Mouvement de stock introuvable"
                                )
                        );

        movementStockRepository.delete(
                movementStock
        );
    }


    // =========================================================
    // CALCUL STOCK RESTANT
    // =========================================================

    private BigDecimal calculerQuantiteRestante(
            LotMP lotMP
    ) {

        BigDecimal quantiteConsommee =
                movementStockRepository.sumQuantiteByLotMP(
                        lotMP.getId()
                );

        return lotMP.getQuantite()
                .subtract(quantiteConsommee);
    }


    // =========================================================
    // VALIDATION QUANTITE
    // =========================================================

    private void verifierQuantiteDisponible(
            BigDecimal quantiteDemandee,
            BigDecimal quantiteDisponible
    ) {

        if (quantiteDemandee.compareTo(
                quantiteDisponible
        ) > 0) {

            throw new RuntimeException(
                    "Quantité insuffisante dans le lot. " +
                            "Quantité disponible : " +
                            quantiteDisponible
            );
        }
    }


    // =========================================================
    // MAPPING RESPONSE
    // =========================================================

    private MovementStockResponse toResponse(
            MovementStock movementStock,
            BigDecimal quantiteRestante
    ) {

        LotMP lotMP =
                movementStock.getLotMP();

        return MovementStockResponse.builder()
                .id(movementStock.getId())
                .quantite(movementStock.getQuantite())
                .dateMouvement(
                        movementStock.getDateMouvement()
                )
                .lotMPId(lotMP.getId())
                .numeroLot(lotMP.getNumeroLot())
                .productionId(
                        movementStock.getProduction().getId()
                )
                .quantiteRestante(
                        quantiteRestante
                )
                .build();
    }
}