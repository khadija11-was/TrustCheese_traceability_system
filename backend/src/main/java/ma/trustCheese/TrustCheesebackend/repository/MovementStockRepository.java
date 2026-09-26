package ma.trustCheese.TrustCheesebackend.repository;


import ma.trustCheese.TrustCheesebackend.entity.MovementStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MovementStockRepository extends JpaRepository<MovementStock, Long> {

    List<MovementStock> findByLotMPId(Long lotMPId);
    List<MovementStock> findByProductionId(Long productionId);

    @Query("""
        SELECT COALESCE(SUM(m.quantite), 0)
        FROM MovementStock m
        WHERE m.lotMP.id = :lotMPId
    """)
    BigDecimal sumQuantiteByLotMP(@Param("lotMPId") Long lotMPId);

    @Query("""
        SELECT COALESCE(SUM(m.quantite), 0)
        FROM MovementStock m
        WHERE m.lotMP.id = :lotMPId
        AND m.id <> :movementId
    """)
    BigDecimal sumQuantiteByLotMPExceptMovement(
            @Param("lotMPId") Long lotMPId,
            @Param("movementId") Long movementId
    );

    List<MovementStock> findByProductionIdOrderByDateMouvementAsc(
            Long productionId
    );
}