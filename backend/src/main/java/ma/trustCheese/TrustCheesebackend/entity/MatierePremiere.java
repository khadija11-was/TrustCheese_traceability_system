package ma.trustCheese.TrustCheesebackend.entity;
import ma.trustCheese.TrustCheesebackend.enums.UniteMesure;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "matiere_premiere")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MatierePremiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;


    @Column(nullable = false)
    private String nom;

    private String description;

    @Enumerated(EnumType.STRING)
    private UniteMesure uniteMesure;
    private BigDecimal seuilStock;
}
