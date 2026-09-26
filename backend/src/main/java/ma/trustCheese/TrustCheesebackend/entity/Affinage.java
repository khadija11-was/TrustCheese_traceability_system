package ma.trustCheese.TrustCheesebackend.entity;


import jakarta.persistence.*;
import lombok.*;
import ma.trustCheese.TrustCheesebackend.enums.StatutAffinage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "affinage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Affinage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String numeroAffinage;

    @Column(nullable = false)
    private LocalDateTime dateDebutReelle;

    private LocalDateTime dateFinReelle;

    @Column(precision = 5, scale = 2)
    private BigDecimal temperature;

    @Column(precision = 5, scale = 2)
    private BigDecimal humidite;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutAffinage statut;
}