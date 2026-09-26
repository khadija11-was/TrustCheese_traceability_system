package ma.trustCheese.TrustCheesebackend.repository;



import ma.trustCheese.TrustCheesebackend.enums.Role;
import ma.trustCheese.TrustCheesebackend.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // Trouver par email (utilisé par Spring Security)
    Optional<Utilisateur> findByEmail(String email);

    // Vérifier si email existe déjà (utile pour le register)
    boolean existsByEmail(String email);

    // Trouver tous les utilisateurs par rôle
    List<Utilisateur> findByRole(Role role);

    // Recherche par nom (pour admin)
    List<Utilisateur> findByNomContainingIgnoreCase(String nom);

    // Trouver les utilisateurs actifs par rôle
//    @Query("SELECT u FROM Utilisateur u WHERE u.role = :role")
//    List<Utilisateur> findActiveByRole(Role role);
}