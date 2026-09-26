package ma.trustCheese.TrustCheesebackend.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fournisseur")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codeFournisseur;

    @Column(nullable = false)
    private String nom;

    private String email;
    private String telephone;
    private String adresse;
}
