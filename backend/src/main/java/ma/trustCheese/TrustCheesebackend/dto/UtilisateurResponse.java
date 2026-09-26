package ma.trustCheese.TrustCheesebackend.dto;

import lombok.*;
import ma.trustCheese.TrustCheesebackend.enums.Role;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurResponse {

    private Long id;
    private String nom;
    private String email;
    private Role role;
    private boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime dernierConnexion;
}
