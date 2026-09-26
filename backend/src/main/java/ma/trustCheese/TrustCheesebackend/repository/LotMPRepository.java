package ma.trustCheese.TrustCheesebackend.repository;


import ma.trustCheese.TrustCheesebackend.entity.LotMP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LotMPRepository extends JpaRepository<LotMP, Long> {

    Optional<LotMP> findByNumeroLot(String numeroLot);

    boolean existsByNumeroLot(String numeroLot);
    @Query(value = "SELECT nextval('lot_mp_code_seq')", nativeQuery = true)
    Long getNextLotNumber();
}