package ma.trustCheese.TrustCheesebackend.entity;



import jakarta.persistence.*;
import lombok.*;
import ma.trustCheese.TrustCheesebackend.enums.TypeEvenementTraceabilite;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "traceability_event",
        indexes = {
                @Index(name = "idx_traceability_numero_lot", columnList = "lot_produit_fini_id"),
                @Index(name = "idx_traceability_event_date", columnList = "event_date"),
                @Index(name = "idx_traceability_event_type", columnList = "event_type")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraceabilityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private TypeEvenementTraceabilite eventType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lot_produit_fini_id", nullable = false)
    private LotProduitFini lotProduitFini;

    @Column(name = "event_date", nullable = false)
    @Builder.Default
    private LocalDateTime eventDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", nullable = false)
    private Utilisateur operator;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    private JsonNode payload;
}