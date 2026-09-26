package ma.trustCheese.TrustCheesebackend.repository;

import ma.trustCheese.TrustCheesebackend.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit, Long> {
    List<Produit> existsByNom(String nom);

    boolean existsByNomIgnoreCase(String nom);
}
