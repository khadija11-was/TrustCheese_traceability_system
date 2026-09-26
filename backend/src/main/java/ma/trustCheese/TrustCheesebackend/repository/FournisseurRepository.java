package ma.trustCheese.TrustCheesebackend.repository;



import ma.trustCheese.TrustCheesebackend.entity.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {

    @Query(value = "SELECT nextval('fournisseur_code_seq')", nativeQuery = true)
    Long getNextCodeNumber();


    Optional<Fournisseur> findByCodeFournisseur(String codeFournisseur);

    boolean existsByCodeFournisseur(String codeFournisseur);

    boolean existsByEmail(String email);

    List<Fournisseur> findByNomContainingIgnoreCase(String nom);

    @Query("SELECT f FROM Fournisseur f WHERE " +
            "LOWER(f.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(f.codeFournisseur) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Fournisseur> search(String keyword);
}