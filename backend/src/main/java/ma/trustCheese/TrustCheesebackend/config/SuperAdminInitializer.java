package ma.trustCheese.TrustCheesebackend.config;


import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.entity.Utilisateur;
import ma.trustCheese.TrustCheesebackend.enums.Role;
import ma.trustCheese.TrustCheesebackend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuperAdminInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.super-admin.email}")
    String adminemail ;

    @Value("${app.super-admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {



        // On ne crée pas le compte s'il existe déjà
        if (utilisateurRepository.existsByEmail(adminemail)) {
            return;
        }

        Utilisateur superAdmin = Utilisateur.builder()
                .nom("admin")
                .email(adminemail)
                .motDePasse(passwordEncoder.encode(adminPassword))
                .role(Role.ADMINISTRATEUR)
                .actif(true)
                .build();

        utilisateurRepository.save(superAdmin);

        System.out.println("==========================================");
        System.out.println(" Super Admin créé avec succès");
        System.out.println("==========================================");
    }
}