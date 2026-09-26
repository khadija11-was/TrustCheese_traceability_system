package ma.trustCheese.TrustCheesebackend.service;

import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.metrics.MetricsResponse;
import ma.trustCheese.TrustCheesebackend.entity.Cuve;
import ma.trustCheese.TrustCheesebackend.entity.Livraison;
import ma.trustCheese.TrustCheesebackend.entity.LotProduitFini;
import ma.trustCheese.TrustCheesebackend.entity.Production;
import ma.trustCheese.TrustCheesebackend.entity.TraceabilityEvent;
import ma.trustCheese.TrustCheesebackend.enums.StatutLivraison;
import ma.trustCheese.TrustCheesebackend.enums.StatutLotProduitFini;
import ma.trustCheese.TrustCheesebackend.enums.StatutProduction;
import ma.trustCheese.TrustCheesebackend.repository.CuveRepository;
import ma.trustCheese.TrustCheesebackend.repository.LivraisonRepository;
import ma.trustCheese.TrustCheesebackend.repository.LotProduitFiniRepository;
import ma.trustCheese.TrustCheesebackend.repository.ProductionRepository;
import ma.trustCheese.TrustCheesebackend.repository.TraceabilityEventRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MetricsService {

    private static final int MAX_UPCOMING_DELIVERIES = 5;
    private static final int MAX_RECENT_ACTIVITIES = 5;

    private final ProductionRepository productionRepository;
    private final LotProduitFiniRepository lotProduitFiniRepository;
    private final LivraisonRepository livraisonRepository;
    private final CuveRepository cuveRepository;
    private final TraceabilityEventRepository traceabilityEventRepository;


    /**
     * Construit toutes les données nécessaires au dashboard.
     */
    public MetricsResponse getMetrics() {

        /*
         * ============================================================
         * 1. KPI
         * ============================================================
         */

        long nbrProductionsEnCours =
                productionRepository.countByStatut(
                        StatutProduction.EN_COURS
                );

        long lotsDisponibles =
                lotProduitFiniRepository.countByStatut(
                        StatutLotProduitFini.LIBERE
                );

        long lotsBloques =
                lotProduitFiniRepository.countByStatut(
                        StatutLotProduitFini.BLOQUE
                );

        LocalDateTime debutJour =
                LocalDate.now().atStartOfDay();

        LocalDateTime debutDemain =
                debutJour.plusDays(1);

        long livraisonsAujourdhui =
                livraisonRepository
                        .findByDateLivraisonPrevueGreaterThanEqualAndDateLivraisonPrevueLessThanOrderByDateLivraisonPrevueAsc(
                                debutJour,
                                debutDemain
                        )
                        .stream()
                        .filter(this::isActiveDelivery)
                        .count();


        MetricsResponse.KpiResponse kpis =
                MetricsResponse.KpiResponse.builder()
                        .productionsEnCours(nbrProductionsEnCours)
                        .lotsDisponibles(lotsDisponibles)
                        .lotsBloques(lotsBloques)
                        .livraisonsAujourdhui(livraisonsAujourdhui)
                        .build();


        /*
         * ============================================================
         * 2. PRODUCTIONS EN COURS
         * ============================================================
         */

        List<Production> productions =
                productionRepository.findByStatut(
                        StatutProduction.EN_COURS
                );

        List<MetricsResponse.ProductionResponse> productionsEnCours =
                productions.stream()
                        .map(this::mapProduction)
                        .toList();


        /*
         * ============================================================
         * 3. CUVES
         * ============================================================
         */

        List<Cuve> cuves =
                cuveRepository.findAll();

        List<MetricsResponse.CuveResponse> cuvesResponse =
                cuves.stream()
                        .map(this::mapCuve)
                        .toList();


        /*
         * ============================================================
         * 4. LIVRAISONS À VENIR
         * ============================================================
         */

        LocalDateTime maintenant =
                LocalDateTime.now();

        List<Livraison> livraisonsAvenir =
                livraisonRepository
                        .findByStatutInAndDateLivraisonPrevueAfterOrderByDateLivraisonPrevueAsc(
                                List.of(
                                        StatutLivraison.PLANIFIEE,
                                        StatutLivraison.EN_PREPARATION
                                ),
                                maintenant,
                                PageRequest.of(
                                        0,
                                        MAX_UPCOMING_DELIVERIES
                                )
                        )
                        .getContent();

        List<MetricsResponse.LivraisonResponse> livraisonsAvenirResponse =
                livraisonsAvenir.stream()
                        .map(this::mapLivraison)
                        .toList();


        /*
         * ============================================================
         * 5. ACTIVITÉS RÉCENTES
         * ============================================================
         */

        List<TraceabilityEvent> recentEvents =
                traceabilityEventRepository
                        .findTop5ByOrderByEventDateDesc();

        List<MetricsResponse.RecentActivityResponse> recentActivities =
                recentEvents.stream()
                        .limit(MAX_RECENT_ACTIVITIES)
                        .map(this::mapRecentActivity)
                        .toList();


        /*
         * ============================================================
         * 6. ALERTES
         * ============================================================
         */

        List<MetricsResponse.AlertResponse> alerts =
                buildAlerts(
                        productions,
                        livraisonsAvenir
                );


        /*
         * ============================================================
         * 7. RESPONSE FINALE
         * ============================================================
         */

        return MetricsResponse.builder()
                .kpis(kpis)
                .alerts(alerts)
                .productionsEnCours(productionsEnCours)
                .cuves(cuvesResponse)
                .livraisonsAvenir(livraisonsAvenirResponse)
                .recentActivities(recentActivities)
                .build();
    }


    // ================================================================
    // MAPPING PRODUCTION
    // ================================================================

    private MetricsResponse.ProductionResponse mapProduction(
            Production production
    ) {

        String produitNom = null;

        if (production.getProduit() != null) {
            produitNom = production.getProduit().getNom();
        }

        String cuveNom = null;

        if (production.getCuve() != null) {
            cuveNom = production.getCuve().getNom();
        }

        return MetricsResponse.ProductionResponse.builder()
                .id(production.getId())
                .numeroProduction(production.getNumeroProduction())
                .produitNom(produitNom)
                .statut(
                        production.getStatut() != null
                                ? production.getStatut().name()
                                : null
                )
                .dateDebut(production.getDateDebut())
                .dateDebutPrevue(production.getDateDebutPrevue())
                .cuveNom(cuveNom)
                .build();
    }


    // ================================================================
    // MAPPING CUVES
    // ================================================================

    private MetricsResponse.CuveResponse mapCuve(
            Cuve cuve
    ) {

        return MetricsResponse.CuveResponse.builder()
                .id(cuve.getId())
                .nom(cuve.getNom())
                .capaciteMaxLitres(cuve.getCapaciteMaxLitres())
                .statutOperationnel(
                        cuve.getStatutOperationnel() != null
                                ? cuve.getStatutOperationnel().name()
                                : null
                )
                .build();
    }


    // ================================================================
    // MAPPING LIVRAISON
    // ================================================================

    private MetricsResponse.LivraisonResponse mapLivraison(
            Livraison livraison
    ) {

        String clientNom = null;

        if (livraison.getClient() != null) {
            clientNom = livraison.getClient().getNom();
        }

        return MetricsResponse.LivraisonResponse.builder()
                .id(livraison.getId())
                .numeroLivraison(livraison.getNumeroLivraison())
                .clientNom(clientNom)
                .dateLivraisonPrevue(
                        livraison.getDateLivraisonPrevue()
                )
                .statut(
                        livraison.getStatut() != null
                                ? livraison.getStatut().name()
                                : null
                )
                .build();
    }


    // ================================================================
    // MAPPING ACTIVITÉ RÉCENTE
    // ================================================================

    private MetricsResponse.RecentActivityResponse mapRecentActivity(
            TraceabilityEvent event
    ) {

        String numeroLot = null;

        if (event.getLotProduitFini() != null) {
            numeroLot =
                    event.getLotProduitFini().getNumeroLot();
        }

        String operatorNom = null;

        if (event.getOperator() != null) {
            operatorNom =
                    event.getOperator().getNom();
        }

        return MetricsResponse.RecentActivityResponse.builder()
                .eventId(event.getEventId())
                .eventType(
                        event.getEventType() != null
                                ? event.getEventType().name()
                                : null
                )
                .eventDate(event.getEventDate())
                .numeroLot(numeroLot)
                .operatorNom(operatorNom)
                .build();
    }


    // ================================================================
    // ALERTES
    // ================================================================

    private List<MetricsResponse.AlertResponse> buildAlerts(
            List<Production> productionsEnCours,
            List<Livraison> livraisonsAvenir
    ) {

        List<MetricsResponse.AlertResponse> alerts =
                new ArrayList<>();


        /*
         * ------------------------------------------------------------
         * A. Lots bloqués
         * ------------------------------------------------------------
         *
         * Pour le moment, on récupère les lots bloqués.
         * Le dashboard peut ensuite afficher une alerte par lot.
         */

        List<LotProduitFini> lotsBloques =
                lotProduitFiniRepository.findByStatut(
                        StatutLotProduitFini.BLOQUE
                );

        for (LotProduitFini lot : lotsBloques) {

            alerts.add(
                    MetricsResponse.AlertResponse.builder()
                            .type("LOT_BLOQUE")
                            .niveau("CRITICAL")
                            .message(
                                    "Le lot "
                                            + lot.getNumeroLot()
                                            + " est bloqué."
                            )
                            .referenceId(lot.getId())
                            .date(null)
                            .build()
            );
        }


        /*
         * ------------------------------------------------------------
         * B. Productions planifiées
         * ------------------------------------------------------------
         *
         * Une production planifiée dont la date prévue est proche
         * peut être affichée comme information dans le dashboard.
         */

        LocalDateTime maintenant =
                LocalDateTime.now();

        LocalDateTime limiteProduction =
                maintenant.plusHours(24);

        List<Production> productionsPlanifiees =
                productionRepository.findByStatut(
                        StatutProduction.PLANIFIEE
                );

        for (Production production : productionsPlanifiees) {

            LocalDateTime datePrevue =
                    production.getDateDebutPrevue();

            if (datePrevue == null) {
                continue;
            }

            if (!datePrevue.isBefore(maintenant)
                    && !datePrevue.isAfter(limiteProduction)) {

                alerts.add(
                        MetricsResponse.AlertResponse.builder()
                                .type("PRODUCTION_PREVUE")
                                .niveau("INFO")
                                .message(
                                        "La production "
                                                + production.getNumeroProduction()
                                                + " est prévue prochainement."
                                )
                                .referenceId(production.getId())
                                .date(datePrevue)
                                .build()
                );
            }
        }


        /*
         * ------------------------------------------------------------
         * C. Livraisons proches non préparées
         * ------------------------------------------------------------
         */

        LocalDateTime limiteLivraison =
                maintenant.plusHours(24);

        for (Livraison livraison : livraisonsAvenir) {

            if (livraison.getDateLivraisonPrevue() == null) {
                continue;
            }

            boolean proche =
                    !livraison.getDateLivraisonPrevue()
                            .isAfter(limiteLivraison);

            boolean nonPreparee =
                    livraison.getStatut()
                            == StatutLivraison.PLANIFIEE;

            if (proche && nonPreparee) {

                alerts.add(
                        MetricsResponse.AlertResponse.builder()
                                .type("LIVRAISON_PROCHE")
                                .niveau("WARNING")
                                .message(
                                        "La livraison "
                                                + livraison.getNumeroLivraison()
                                                + " est prévue prochainement "
                                                + "et n'est pas encore préparée."
                                )
                                .referenceId(livraison.getId())
                                .date(
                                        livraison.getDateLivraisonPrevue()
                                )
                                .build()
                );
            }
        }


        /*
         * ------------------------------------------------------------
         * Tri des alertes
         * ------------------------------------------------------------
         *
         * Les alertes les plus proches sont affichées en premier.
         */

        alerts.sort(
                Comparator.comparing(
                        MetricsResponse.AlertResponse::getDate,
                        Comparator.nullsLast(Comparator.naturalOrder())
                )
        );

        return alerts;
    }


    // ================================================================
    // UTILITAIRE LIVRAISON
    // ================================================================

    private boolean isActiveDelivery(
            Livraison livraison
    ) {

        if (livraison == null
                || livraison.getStatut() == null) {
            return false;
        }

        return livraison.getStatut() == StatutLivraison.PLANIFIEE
                || livraison.getStatut() == StatutLivraison.EN_PREPARATION
                || livraison.getStatut() == StatutLivraison.EXPEDIEE;
    }
}

