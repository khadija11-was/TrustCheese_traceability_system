package ma.trustCheese.TrustCheesebackend.repository;

import ma.trustCheese.TrustCheesebackend.entity.TraceabilityEvent;
import ma.trustCheese.TrustCheesebackend.enums.TypeEvenementTraceabilite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TraceabilityEventRepository
        extends JpaRepository<TraceabilityEvent, Long> {

    List<TraceabilityEvent> findByLotProduitFiniIdOrderByEventDateAsc(
            Long lotProduitFiniId
    );

    List<TraceabilityEvent> findByLotProduitFiniIdAndEventTypeOrderByEventDateAsc(
            Long lotProduitFiniId,
            TypeEvenementTraceabilite eventType
    );

    List<TraceabilityEvent> findTop5ByOrderByEventDateDesc();



}