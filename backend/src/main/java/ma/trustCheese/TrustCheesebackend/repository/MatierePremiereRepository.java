package ma.trustCheese.TrustCheesebackend.repository;


import ma.trustCheese.TrustCheesebackend.entity.MatierePremiere;
import ma.trustCheese.TrustCheesebackend.enums.UniteMesure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatierePremiereRepository extends JpaRepository<MatierePremiere, Long> {

    boolean existsByNom(String nom);

    List<MatierePremiere> findByUniteMesure(UniteMesure uniteMesure);

    List<MatierePremiere> findByNomContainingIgnoreCase(String nom);

    @Query("SELECT m FROM MatierePremiere m WHERE " +
            "LOWER(m.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MatierePremiere> search(String keyword);
}