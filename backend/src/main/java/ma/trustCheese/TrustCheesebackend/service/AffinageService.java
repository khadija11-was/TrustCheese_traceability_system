package ma.trustCheese.TrustCheesebackend.service;



import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.AffinageRequest;
import ma.trustCheese.TrustCheesebackend.dto.AffinageResponse;
import ma.trustCheese.TrustCheesebackend.entity.Affinage;
import ma.trustCheese.TrustCheesebackend.entity.LotProduitFini;
import ma.trustCheese.TrustCheesebackend.entity.Production;
import ma.trustCheese.TrustCheesebackend.enums.StatutAffinage;
import ma.trustCheese.TrustCheesebackend.enums.StatutLotProduitFini;
import ma.trustCheese.TrustCheesebackend.repository.AffinageRepository;
import ma.trustCheese.TrustCheesebackend.repository.LotProduitFiniRepository;
import ma.trustCheese.TrustCheesebackend.repository.ProductionRepository;
import ma.trustCheese.TrustCheesebackend.service.traceability.TraceabilityEventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AffinageService {

    private final AffinageRepository affinageRepository;
    private final ProductionRepository productionRepository;
    private final LotProduitFiniRepository lotProduitFiniRepository;
    private final TraceabilityEventService traceabilityEventService;


    /**
     * Crée un affinage et affecte automatiquement
     * tous les lots disponibles de la production.
     */
    public AffinageResponse createAffinage(AffinageRequest request) {

        // 1. Vérifier que la production existe
        Production production = productionRepository.findById(request.getProductionId())
                .orElseThrow(() -> new RuntimeException(
                        "Production introuvable avec l'id : "
                                + request.getProductionId()
                ));


        // 2. La production doit être terminée
        if (production.getStatut() != ma.trustCheese.TrustCheesebackend.enums.StatutProduction.TERMINEE) {
            throw new RuntimeException(
                    "L'affinage ne peut être créé que pour une production terminée."
            );
        }


        // 3. Récupérer tous les lots disponibles de cette production
        List<LotProduitFini> lotsDisponibles =
                lotProduitFiniRepository.findByProductionIdAndStatut(
                        production.getId(),
                        StatutLotProduitFini.DISPONIBLE
                );


        // 4. Vérifier qu'il existe des lots disponibles
        if (lotsDisponibles.isEmpty()) {
            throw new RuntimeException(
                    "Aucun lot disponible pour cette production."
            );
        }


        // 6. Générer le numéro d'affinage
        String numeroAffinage = generateNumeroAffinage(
                production.getDateFin()
        );


        // 7. Créer l'affinage
        LocalDateTime now = LocalDateTime.now();

        Affinage affinage = Affinage.builder()
                .numeroAffinage(numeroAffinage)
                .dateDebutReelle(now)
                .temperature(request.getTemperature())
                .humidite(request.getHumidite())
                .observations(request.getObservations())
                .statut(StatutAffinage.EN_COURS)
                .build();

        Affinage savedAffinage = affinageRepository.save(affinage);


        // 8. Affecter tous les lots à l'affinage
        for (LotProduitFini lot : lotsDisponibles) {

            lot.setAffinage(savedAffinage);
            lot.setStatut(StatutLotProduitFini.EN_AFFINAGE);
        }

        lotProduitFiniRepository.saveAll(lotsDisponibles);
        traceabilityEventService.createAffinageStartEvents(
                lotsDisponibles,
                savedAffinage
        );


        // 9. Retourner la réponse
        return mapToResponse(savedAffinage, production, lotsDisponibles.size());
    }


    /**
     * Termine un affinage.
     */
    public AffinageResponse terminerAffinage(Long affinageId) {

        Affinage affinage = getAffinageEntity(affinageId);

        if (affinage.getStatut() != StatutAffinage.EN_COURS) {
            throw new RuntimeException(
                    "Seul un affinage en cours peut être terminé."
            );
        }

        affinage.setDateFinReelle(LocalDateTime.now());
        affinage.setStatut(StatutAffinage.TERMINE);

        Affinage savedAffinage = affinageRepository.save(affinage);

        List<LotProduitFini> lots =
                lotProduitFiniRepository.findByAffinageId(affinageId);

        Production production = null;

        if (!lots.isEmpty()) {
            production = lots.get(0).getProduction();
        }

        traceabilityEventService.createAffinageEndEvents(
                lots,
                savedAffinage
        );

        return mapToResponse(
                savedAffinage,
                production,
                lots.size()
        );
    }


    /**
     * Annule un affinage.
     */
    public AffinageResponse annulerAffinage(Long affinageId) {

        Affinage affinage = getAffinageEntity(affinageId);


        if (affinage.getStatut() == StatutAffinage.TERMINE) {
            throw new RuntimeException(
                    "Un affinage terminé ne peut pas être annulé."
            );
        }


        if (affinage.getStatut() == StatutAffinage.ANNULE) {
            throw new RuntimeException(
                    "L'affinage est déjà annulé."
            );
        }


        affinage.setStatut(StatutAffinage.ANNULE);


        Affinage savedAffinage = affinageRepository.save(affinage);


        List<LotProduitFini> lots =
                lotProduitFiniRepository.findByAffinageId(affinageId);


        // Les lots doivent redevenir disponibles
        // puisqu'ils ne sont plus en affinage.
        for (LotProduitFini lot : lots) {
            lot.setAffinage(null);
            lot.setStatut(StatutLotProduitFini.DISPONIBLE);
        }

        lotProduitFiniRepository.saveAll(lots);


        Production production = null;

        if (!lots.isEmpty()) {
            production = lots.get(0).getProduction();
        }


        return mapToResponse(
                savedAffinage,
                production,
                lots.size()
        );
    }


    /**
     * Retourne tous les affinages.
     */
    @Transactional(readOnly = true)
    public List<AffinageResponse> getAllAffinages() {

        return affinageRepository.findAll()
                .stream()
                .map(affinage -> {

                    List<LotProduitFini> lots =
                            lotProduitFiniRepository.findByAffinageId(
                                    affinage.getId()
                            );

                    Production production = null;

                    if (!lots.isEmpty()) {
                        production = lots.get(0).getProduction();
                    }

                    return mapToResponse(
                            affinage,
                            production,
                            lots.size()
                    );
                })
                .toList();
    }


    /**
     * Retourne un affinage par son id.
     */
    @Transactional(readOnly = true)
    public AffinageResponse getAffinageById(Long id) {

        Affinage affinage = getAffinageEntity(id);

        List<LotProduitFini> lots =
                lotProduitFiniRepository.findByAffinageId(id);

        Production production = null;

        if (!lots.isEmpty()) {
            production = lots.get(0).getProduction();
        }

        return mapToResponse(
                affinage,
                production,
                lots.size()
        );
    }


    /**
     * Retourne les affinages selon leur statut.
     */
    @Transactional(readOnly = true)
    public List<AffinageResponse> getAffinagesByStatut(
            StatutAffinage statut
    ) {

        return affinageRepository.findByStatut(statut)
                .stream()
                .map(affinage -> {

                    List<LotProduitFini> lots =
                            lotProduitFiniRepository.findByAffinageId(
                                    affinage.getId()
                            );

                    Production production = null;

                    if (!lots.isEmpty()) {
                        production = lots.get(0).getProduction();
                    }

                    return mapToResponse(
                            affinage,
                            production,
                            lots.size()
                    );
                })
                .toList();
    }


    /**
     * Récupère l'entité Affinage.
     */
    private Affinage getAffinageEntity(Long id) {

        return affinageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Affinage introuvable avec l'id : " + id
                ));
    }


    /**
     * Génère le numéro d'affinage.
     *
     * Exemple :
     * AFF-2026-001
     */
    private String generateNumeroAffinage(
            LocalDateTime dateProduction
    ) {

        int annee = dateProduction != null
                ? dateProduction.getYear()
                : LocalDateTime.now().getYear();

        long sequence =
                affinageRepository.getNextAffinageSequence();

        return String.format(
                "AFF-%d-%03d",
                annee,
                sequence
        );
    }


    /**
     * Conversion Entity → Response.
     */
    private AffinageResponse mapToResponse(
            Affinage affinage,
            Production production,
            int nombreLots
    ) {

        return AffinageResponse.builder()
                .id(affinage.getId())
                .numeroAffinage(affinage.getNumeroAffinage())
                .dateDebutReelle(affinage.getDateDebutReelle())
                .dateFinReelle(affinage.getDateFinReelle())
                .temperature(affinage.getTemperature())
                .humidite(affinage.getHumidite())
                .observations(affinage.getObservations())
                .statut(affinage.getStatut().name())
                .nombreLots(nombreLots)
                .productionId(
                        production != null
                                ? production.getId()
                                : null
                )
                .numeroProduction(
                        production != null
                                ? production.getNumeroProduction()
                                : null
                )
                .build();
    }
}