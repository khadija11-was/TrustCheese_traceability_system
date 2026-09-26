package ma.trustCheese.TrustCheesebackend.repository;

import ma.trustCheese.TrustCheesebackend.entity.LotProduitFini;
import ma.trustCheese.TrustCheesebackend.enums.StatutLotProduitFini;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LotProduitFiniRepository extends JpaRepository<LotProduitFini, Long> {
    List<LotProduitFini> findByProductionId(Long productionId);

    boolean existsByNumeroLot(String numeroLot);

    long countByProductionId(Long productionId);
    Optional<LotProduitFini> findByNumeroLot(String numeroLot);

    List<LotProduitFini> findByProductionIdAndStatut(
            Long productionId,
            StatutLotProduitFini statut
    );
    List<LotProduitFini> findByAffinageId(Long affinageId);


    @Query(value = "SELECT nextval('lot_sequence')", nativeQuery = true)
    long getNextLotSequence();

    List<LotProduitFini>
    findByProductionProduitIdAndStatutOrderByProductionDateFinAsc(
            Long produitId,
            StatutLotProduitFini statut
    );

    long countByStatut(StatutLotProduitFini statut);
    List<LotProduitFini> findByStatut(StatutLotProduitFini statut);
}
