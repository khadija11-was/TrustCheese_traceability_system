package ma.trustCheese.TrustCheesebackend.service;

import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.ProductionRequest;
import ma.trustCheese.TrustCheesebackend.dto.ProductionResponse;
import ma.trustCheese.TrustCheesebackend.dto.ProductionMesureRequest;
import ma.trustCheese.TrustCheesebackend.entity.*;
import ma.trustCheese.TrustCheesebackend.enums.StatutProduction;
import ma.trustCheese.TrustCheesebackend.repository.ProductionRepository;
import ma.trustCheese.TrustCheesebackend.repository.ProduitRepository;
import ma.trustCheese.TrustCheesebackend.repository.UtilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ma.trustCheese.TrustCheesebackend.enums.StatutOperationnel;
import ma.trustCheese.TrustCheesebackend.repository.CuveRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductionService {

    private final ProductionRepository productionRepository;
    private final ProduitRepository produitRepository;
    private final LotProduitFiniService lotProduitFiniService;
    private final CuveRepository cuveRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final TraceabilityEventService traceabilityEventService;

    // ============================================================
    // 1. PLANIFIER UNE PRODUCTION
    // ============================================================

    public ProductionResponse planProduction(ProductionRequest request) {

        if (request == null) {
            throw new RuntimeException(
                    "Les données de production sont obligatoires."
            );
        }

        if (request.getDateDebutPrevue() == null) {
            throw new RuntimeException(
                    "La date de début prévue est obligatoire."
            );
        }

        if (request.getDateFinPrevue() != null
                && request.getDateFinPrevue()
                .isBefore(request.getDateDebutPrevue())) {

            throw new RuntimeException(
                    "La date de fin prévue doit être postérieure " +
                            "à la date de début prévue."
            );
        }

        Produit produit = produitRepository.findById(request.getProduitId())
                .orElseThrow(() -> new RuntimeException(
                        "Produit introuvable avec l'id : "
                                + request.getProduitId()
                ));
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        System.out.println(
                "Utilisateur authentifié : "
                        + authentication.getName()
        );

        String email = authentication.getName();

        Utilisateur operateur =
                utilisateurRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Utilisateur connecté introuvable."
                                )
                        );

        String numeroProduction =
                generateNumeroProduction(
                        request.getDateDebutPrevue()
                );

        Production production = Production.builder()
                .numeroProduction(numeroProduction)

                // Dates prévues
                .dateDebutPrevue(request.getDateDebutPrevue())
                .dateFinPrevue(request.getDateFinPrevue())

                // Dates réelles
                .dateDebut(null)
                .dateFin(null)

                // Résultat de production
                .nombreLotsProduits(null)

                // État initial
                .statut(StatutProduction.PLANIFIEE)

                // Produit fabriqué
                .produit(produit)
                .operateur(operateur)

                .build();

        Production savedProduction =
                productionRepository.save(production);

        /*
         * TODO :
         * Enregistrer l'événement de traçabilité :
         *
         * PRODUCTION_PLANIFIEE
         */

        return mapToResponse(savedProduction);
    }


    // ============================================================
    // 2. DÉMARRER UNE PRODUCTION PLANIFIÉE
    // ============================================================

    public ProductionResponse startProduction(Long productionId, Long cuveId, BigDecimal temperatureCuve,
                                              BigDecimal phCuve) {

        Production production =
                getProductionEntity(productionId);

        if (production.getStatut()
                != StatutProduction.PLANIFIEE) {

            throw new RuntimeException(
                    "Seule une production planifiée peut être démarrée."
            );
        }
        // --------------------------------------------------------
        // Vérification de la cuve
        // --------------------------------------------------------

        if (cuveId == null) {

            throw new RuntimeException(
                    "La cuve est obligatoire pour démarrer la production."
            );
        }

        Cuve cuve = cuveRepository.findById(cuveId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cuve introuvable avec l'id : "
                                        + cuveId
                        )
                );
        // --------------------------------------------------------
        // Vérifier le statut opérationnel de la cuve
        // --------------------------------------------------------

        if (cuve.getStatutOperationnel()
                != StatutOperationnel.DISPONIBLE) {

            throw new RuntimeException(
                    "La cuve " + cuve.getNom()
                            + " n'est pas disponible. "
                            + "Son statut actuel est : "
                            + cuve.getStatutOperationnel()
            );
        }

        // --------------------------------------------------------
        // Vérifier si la cuve est déjà utilisée
        // par une production en cours
        // --------------------------------------------------------

        boolean cuveDejaUtilisee =
                productionRepository.existsProductionEnCoursPourCuve(
                        cuveId,
                        productionId
                );

        if (cuveDejaUtilisee) {

            throw new RuntimeException(
                    "La cuve " + cuve.getNom()
                            + " est déjà utilisée par une autre production."
            );
        }


        LocalDateTime now = LocalDateTime.now();

        production.setDateDebut(now);
        production.setStatut(StatutProduction.EN_COURS);
        production.setCuve(cuve);
        production.setTemperatureCuve(temperatureCuve);
        production.setPhCuve(phCuve);
        cuve.setStatutOperationnel(StatutOperationnel.CHAUFFE);

        Production savedProduction =
                productionRepository.save(production);

        /*
         * TODO :
         * Enregistrer l'événement :
         *
         * PRODUCTION_DEMARREE
         */

        return mapToResponse(savedProduction);
    }


    // ============================================================
    // 3. TERMINER UNE PRODUCTION
    // ============================================================

    public ProductionResponse completeProduction(
            Long productionId,
            Integer nombreLotsProduits) {

        Production production =
                getProductionEntity(productionId);

        // --------------------------------------------------------
        // Vérification de l'état
        // --------------------------------------------------------

        if (production.getStatut()
                != StatutProduction.EN_COURS) {

            throw new RuntimeException(
                    "Seule une production en cours peut être terminée."
            );
        }

        // --------------------------------------------------------
        // Vérification du nombre de lots
        // --------------------------------------------------------

        if (nombreLotsProduits == null
                || nombreLotsProduits <= 0) {

            throw new RuntimeException(
                    "Le nombre de lots produits doit être supérieur à 0."
            );
        }

        // --------------------------------------------------------
        // Vérification du produit
        // --------------------------------------------------------

        if (production.getProduit() == null) {

            throw new RuntimeException(
                    "Aucun produit n'est associé à cette production."
            );
        }

        // --------------------------------------------------------
        // Vérification de la durée de conservation
        // --------------------------------------------------------

        if (production.getProduit()
                .getDureeConservationJours() == null
                || production.getProduit()
                .getDureeConservationJours() <= 0) {

            throw new RuntimeException(
                    "La durée de conservation du produit doit être définie."
            );
        }

        // --------------------------------------------------------
        // Finalisation de la production
        // --------------------------------------------------------

        LocalDateTime dateFinReelle =
                LocalDateTime.now();

        production.setNombreLotsProduits(nombreLotsProduits);
        production.setDateFin(dateFinReelle);
        production.setStatut(StatutProduction.TERMINEE);
        Cuve cuve = production.getCuve();

        if (cuve != null) {
            cuve.setStatutOperationnel(
                    StatutOperationnel.DISPONIBLE
            );

            cuveRepository.save(cuve);
        }

        Production completedProduction =
                productionRepository.save(production);

        // --------------------------------------------------------
        // Génération automatique des lots
        // --------------------------------------------------------
        //
        // Exemple :
        //
        // nombreLotsProduits = 500
        //
        // LOT-000001
        // LOT-000002
        // ...
        // LOT-000500
        //
        // Les lots sont liés à cette production par production_id.
        //
        // --------------------------------------------------------

        List<LotProduitFini> lots =
                lotProduitFiniService.generateLots(
                        completedProduction,
                        nombreLotsProduits
                );

        traceabilityEventService.createProductionEvents(
                lots,
                completedProduction
        );

        return mapToResponse(completedProduction);
    }

        public ProductionResponse updateMesures(
                        Long productionId,
                        ProductionMesureRequest request) {

                Production production = getProductionEntity(productionId);

                if (production.getStatut() != StatutProduction.EN_COURS) {
                        throw new RuntimeException(
                                        "Les mesures peuvent uniquement être saisies pour une production en cours."
                        );
                }

                production.setTemperatureCuve(request.getTemperatureCuve());
                production.setPhCuve(request.getPhCuve());

                return mapToResponse(productionRepository.save(production));
        }


    // ============================================================
    // 4. ANNULER UNE PRODUCTION
    // ============================================================

    public ProductionResponse cancelProduction(
            Long productionId) {

        Production production =
                getProductionEntity(productionId);

        // --------------------------------------------------------
        // Vérifier que la production n'est pas déjà terminée
        // --------------------------------------------------------

        if (production.getStatut()
                == StatutProduction.TERMINEE) {

            throw new RuntimeException(
                    "Une production terminée ne peut pas être annulée."
            );
        }

        // --------------------------------------------------------
        // Vérifier qu'elle n'est pas déjà annulée
        // --------------------------------------------------------

        if (production.getStatut()
                == StatutProduction.ANNULEE) {

            throw new RuntimeException(
                    "La production est déjà annulée."
            );
        }

        // --------------------------------------------------------
        // Annulation
        // --------------------------------------------------------
        production.setDateFin(LocalDateTime.now());

        production.setStatut(
                StatutProduction.ANNULEE
        );

        Cuve cuve = production.getCuve();

        if (cuve != null) {
            cuve.setStatutOperationnel(
                    StatutOperationnel.DISPONIBLE
            );

            cuveRepository.save(cuve);
        }


        Production cancelledProduction =
                productionRepository.save(production);

        /*
         * TODO :
         * Enregistrer l'événement :
         *
         * PRODUCTION_ANNULEE
         */

        return mapToResponse(cancelledProduction);
    }


    // ============================================================
    // 5. RÉCUPÉRER TOUTES LES PRODUCTIONS
    // ============================================================

    @Transactional(readOnly = true)
    public List<ProductionResponse> getAllProductions() {

        return productionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // ============================================================
    // 6. RÉCUPÉRER UNE PRODUCTION PAR ID
    // ============================================================

    @Transactional(readOnly = true)
    public ProductionResponse getProductionById(
            Long id) {

        Production production =
                getProductionEntity(id);

        return mapToResponse(production);
    }


    // ============================================================
    // 7. MÉTHODE INTERNE : RÉCUPÉRER UNE ENTITY
    // ============================================================

    private Production getProductionEntity(
            Long productionId) {

        return productionRepository.findById(productionId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Production introuvable avec l'id : "
                                        + productionId
                        )
                );
    }


    // ============================================================
    // 8. GÉNÉRATION DU NUMÉRO DE PRODUCTION
    // ============================================================

    private String generateNumeroProduction(
            LocalDateTime dateDebutPrevue) {

        int annee =
                dateDebutPrevue.getYear();

        long nextNumber =
                productionRepository
                        .getNextProductionNumber();

        return String.format(
                "PROD-%d-%03d",
                annee,
                nextNumber
        );
    }


    // ============================================================
    // 9. MAPPING ENTITY → RESPONSE
    // ============================================================

    private ProductionResponse mapToResponse(
            Production production) {

        return ProductionResponse.builder()
                .id(production.getId())

                .numeroProduction(
                        production.getNumeroProduction()
                )

                // Dates prévues
                .dateDebutPrevue(
                        production.getDateDebutPrevue()
                )

                .dateFinPrevue(
                        production.getDateFinPrevue()
                )

                // Dates réelles
                .dateDebut(
                        production.getDateDebut()
                )

                .dateFin(
                        production.getDateFin()
                )

                // Résultat
                .nombreLotsProduits(
                        production.getNombreLotsProduits()
                )

                // Statut
                .statut(
                        production.getStatut()
                )

                // Produit
                .produitId(
                        production.getProduit().getId()
                )

                .produitNom(
                        production.getProduit().getNom()
                )
                .operateurId(
                        production.getOperateur() != null
                                ? production.getOperateur().getId()
                                : null
                )

                .operateurNom(
                        production.getOperateur() != null
                                ? production.getOperateur().getNom()
                                : null
                )
                .cuveId(
                        production.getCuve() != null
                                ? production.getCuve().getId()
                                : null
                )

                .cuveNom(
                        production.getCuve() != null
                                ? production.getCuve().getNom()
                                : null
                )

                .temperatureCuve(
                        production.getTemperatureCuve()
                )

                .phCuve(
                        production.getPhCuve()
                )


                .build();
    }
}

