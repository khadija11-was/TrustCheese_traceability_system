package ma.trustCheese.TrustCheesebackend.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ma.trustCheese.TrustCheesebackend.enums.Role;

@Entity
@Table(name = "utilisateur")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    private LocalDateTime dernierConnexion;

    @Column(nullable = false)
    private boolean actif;

    @PrePersist
    protected void onCreate() {

        this.dateCreation = LocalDateTime.now();
        this.actif = true;
    }

    @OneToMany(mappedBy = "utilisateur")
    @Builder.Default
    private List<ControleQualite> controlesQualite = new ArrayList<>();
}
