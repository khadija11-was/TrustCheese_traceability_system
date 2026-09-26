package ma.trustCheese.TrustCheesebackend.config;


import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.entity.Utilisateur;
import ma.trustCheese.TrustCheesebackend.enums.Role;
import ma.trustCheese.TrustCheesebackend.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuperAdminInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String email = "superadmin@trustcheese.ma";

        // On ne crée pas le compte s'il existe déjà
        if (utilisateurRepository.existsByEmail(email)) {
            return;
        }

        Utilisateur superAdmin = Utilisateur.builder()
                .nom("admin")
                .email(email)
                .motDePasse(passwordEncoder.encode("Admin@1234"))
                .role(Role.ADMINISTRATEUR)
                .actif(true)
                .build();

        utilisateurRepository.save(superAdmin);

        System.out.println("==========================================");
        System.out.println(" Super Admin créé avec succès");
        System.out.println(" Email : " + email);
        System.out.println(" Mot de passe : Admin@1234");
        System.out.println("==========================================");
    }
}