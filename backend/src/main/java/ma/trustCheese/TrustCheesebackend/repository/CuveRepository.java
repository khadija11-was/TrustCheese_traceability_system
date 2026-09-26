package ma.trustCheese.TrustCheesebackend.repository;

import ma.trustCheese.TrustCheesebackend.entity.Cuve;
import ma.trustCheese.TrustCheesebackend.enums.StatutOperationnel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuveRepository extends JpaRepository<Cuve, Long> {
    Boolean existsByNomIgnoreCase(String nom);
    List<Cuve> findByStatutOperationnel(StatutOperationnel statut);
}
