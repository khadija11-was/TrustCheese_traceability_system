package ma.trustCheese.TrustCheesebackend.entity;


import jakarta.persistence.*;
import lombok.*;
import ma.trustCheese.TrustCheesebackend.enums.StatutOperationnel;

import java.math.BigDecimal;

@Entity
@Table(name = "cuve")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(name = "capacite_max_litres", nullable = false, precision = 10, scale = 2)
    private BigDecimal capaciteMaxLitres;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_operationnel", nullable = false)
    @Builder.Default
    private StatutOperationnel statutOperationnel = StatutOperationnel.DISPONIBLE;
}