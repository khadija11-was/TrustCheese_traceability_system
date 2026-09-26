package ma.trustCheese.TrustCheesebackend.repository;


import ma.trustCheese.TrustCheesebackend.entity.ControleQualite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ControleQualiteRepository extends JpaRepository<ControleQualite, Long> {

    Optional<ControleQualite> findByNumeroControle(String numeroControle);

    boolean existsByNumeroControle(String numeroControle);

    List<ControleQualite> findByLotProduitFiniId(Long lotProduitFiniId);

    List<ControleQualite> findByUtilisateurId(Long utilisateurId);

    @Query(value = "SELECT nextval('controle_qualite_code_seq')", nativeQuery = true)
    long getNextControleQualiteSequence();
}
