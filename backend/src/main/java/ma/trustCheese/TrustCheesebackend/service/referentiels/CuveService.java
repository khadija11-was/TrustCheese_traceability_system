package ma.trustCheese.TrustCheesebackend.service.referentiels;



import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.entity.Cuve;
import ma.trustCheese.TrustCheesebackend.enums.StatutOperationnel;
import ma.trustCheese.TrustCheesebackend.repository.CuveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CuveService {

    private final CuveRepository cuveRepository;

    /**
     * Créer une nouvelle cuve.
     */
    public Cuve createCuve(Cuve cuve) {

        validateCuve(cuve);

        if (cuveRepository.existsByNomIgnoreCase(cuve.getNom())) {
            throw new IllegalArgumentException(
                    "Une cuve avec le nom '" + cuve.getNom() + "' existe déjà."
            );
        }

        // Si aucun statut n'est fourni, la cuve est disponible par défaut
        if (cuve.getStatutOperationnel() == null) {
            cuve.setStatutOperationnel(StatutOperationnel.DISPONIBLE);
        }

        return cuveRepository.save(cuve);
    }

    /**
     * Récupérer toutes les cuves.
     */
    @Transactional(readOnly = true)
    public List<Cuve> getAllCuves() {
        return cuveRepository.findAll();
    }

    /**
     * Récupérer une cuve par son ID.
     */
    @Transactional(readOnly = true)
    public Cuve getCuveById(Long id) {

        return cuveRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cuve introuvable avec l'id : " + id
                        )
                );
    }

    /**
     * Modifier une cuve.
     */
    public Cuve updateCuve(Long id, Cuve cuveDetails) {

        Cuve cuve = getCuveById(id);

        validateCuve(cuveDetails);

        // Vérifier que le nouveau nom n'appartient pas à une autre cuve
        if (!cuve.getNom().equalsIgnoreCase(cuveDetails.getNom())
                && cuveRepository.existsByNomIgnoreCase(cuveDetails.getNom())) {

            throw new IllegalArgumentException(
                    "Une cuve avec le nom '" + cuveDetails.getNom() + "' existe déjà."
            );
        }

        cuve.setNom(cuveDetails.getNom());
        cuve.setCapaciteMaxLitres(cuveDetails.getCapaciteMaxLitres());

        if (cuveDetails.getStatutOperationnel() != null) {
            cuve.setStatutOperationnel(
                    cuveDetails.getStatutOperationnel()
            );
        }

        return cuveRepository.save(cuve);
    }

    /**
     * Modifier uniquement le statut opérationnel d'une cuve.
     */
    public Cuve updateStatut(Long id, StatutOperationnel statut) {

        if (statut == null) {
            throw new IllegalArgumentException(
                    "Le statut opérationnel ne peut pas être null."
            );
        }

        Cuve cuve = getCuveById(id);

        cuve.setStatutOperationnel(statut);

        return cuveRepository.save(cuve);
    }

    /**
     * Supprimer une cuve.
     */
    public void deleteCuve(Long id) {

        Cuve cuve = getCuveById(id);

        cuveRepository.delete(cuve);
    }

    /**
     * Récupérer uniquement les cuves disponibles.
     */
    @Transactional(readOnly = true)
    public List<Cuve> getCuvesDisponibles() {

        return cuveRepository.findByStatutOperationnel(
                StatutOperationnel.DISPONIBLE
        );
    }

    /**
     * Vérifier les données d'une cuve.
     */
    private void validateCuve(Cuve cuve) {

        if (cuve == null) {
            throw new IllegalArgumentException(
                    "Les informations de la cuve sont obligatoires."
            );
        }

        if (cuve.getNom() == null || cuve.getNom().isBlank()) {
            throw new IllegalArgumentException(
                    "Le nom de la cuve est obligatoire."
            );
        }

        if (cuve.getCapaciteMaxLitres() == null
                || cuve.getCapaciteMaxLitres().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "La capacité maximale doit être supérieure à zéro."
            );
        }
    }
}