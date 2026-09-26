package ma.trustCheese.TrustCheesebackend.repository;



import ma.trustCheese.TrustCheesebackend.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByCodeClient(String codeClient);

    boolean existsByCodeClient(String codeClient);
}
