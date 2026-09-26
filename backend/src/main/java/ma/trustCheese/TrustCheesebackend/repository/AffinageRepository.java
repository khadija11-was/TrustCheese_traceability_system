package ma.trustCheese.TrustCheesebackend.repository;


import ma.trustCheese.TrustCheesebackend.entity.Affinage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AffinageRepository extends JpaRepository<Affinage, Long> {

    Optional<Affinage> findByNumeroAffinage(String numeroAffinage);

    boolean existsByNumeroAffinage(String numeroAffinage);

    List<Affinage> findByStatut(
            ma.trustCheese.TrustCheesebackend.enums.StatutAffinage statut
    );

    @Query(value = "SELECT nextval('affinage_code_seq')", nativeQuery = true)
    long getNextAffinageSequence();
}