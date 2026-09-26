package ma.trustCheese.TrustCheesebackend.repository;



import ma.trustCheese.TrustCheesebackend.entity.Production;
import ma.trustCheese.TrustCheesebackend.enums.StatutProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductionRepository extends JpaRepository<Production, Long> {

    boolean existsByNumeroProduction(String numeroProduction);
    @Query(value = "SELECT nextval('production_code_seq')", nativeQuery = true)
    Long getNextProductionNumber();

    @Query("""
    SELECT COUNT(p) > 0
    FROM Production p
    WHERE p.cuve.id = :cuveId
      AND p.dateDebut IS NOT NULL
      AND p.dateFin IS NULL
      AND (:productionId IS NULL OR p.id <> :productionId)
""")
    boolean existsProductionEnCoursPourCuve(
            @Param("cuveId") Long cuveId,
            @Param("productionId") Long productionId
    );

    long countByStatut(StatutProduction statut);

    List<Production> findByStatut(StatutProduction statut);

}