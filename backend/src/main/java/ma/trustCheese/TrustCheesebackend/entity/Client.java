package ma.trustCheese.TrustCheesebackend.entity;


import jakarta.persistence.*;
import lombok.*;
import ma.trustCheese.TrustCheesebackend.enums.TypeClient;

@Entity
@Table(
        name = "client",
        indexes = {
                @Index(name = "idx_client_nom", columnList = "nom"),
                @Index(name = "idx_client_code", columnList = "code_client")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Code unique permettant d'identifier rapidement le client.
     * Exemple : CLI-0001
     */
    @Column(name = "code_client", nullable = false, unique = true, length = 50)
    private String codeClient;

    /**
     * Nom du client ou de l'entreprise.
     */
    @Column(nullable = false, length = 150)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_client", nullable = false, length = 30)
    private TypeClient typeClient;

    @Column(length = 30)
    private String telephone;

    @Column(length = 150)
    private String email;



    @Column(length = 255)
    private String adresse;

    @Column(length = 100)
    private String ville;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;
}


