package ma.trustCheese.TrustCheesebackend.repository;

import ma.trustCheese.TrustCheesebackend.entity.Livraison;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ma.trustCheese.TrustCheesebackend.enums.StatutLivraison;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LivraisonRepository
        extends JpaRepository<Livraison, Long> {

    Optional<Livraison> findByNumeroLivraison(
            String numeroLivraison
    );

    boolean existsByNumeroLivraison(
            String numeroLivraison
    );

    List<Livraison> findByClientIdOrderByDateLivraisonPrevueAsc(
            Long clientId
    );

    List<Livraison> findByStatutOrderByDateLivraisonPrevueAsc(
            StatutLivraison statut
    );

    List<Livraison>
    findByDateLivraisonPrevueGreaterThanEqualAndDateLivraisonPrevueLessThanOrderByDateLivraisonPrevueAsc(
            LocalDateTime debut,
            LocalDateTime fin
    );
    Page<Livraison> findByStatutInAndDateLivraisonPrevueAfterOrderByDateLivraisonPrevueAsc(
            List<StatutLivraison> statuts,
            LocalDateTime date,
            Pageable pageable
    );
}