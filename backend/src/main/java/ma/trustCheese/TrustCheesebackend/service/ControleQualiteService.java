package ma.trustCheese.TrustCheesebackend.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.ControleQualiteRequest;
import ma.trustCheese.TrustCheesebackend.dto.ControleQualiteResponse;
import ma.trustCheese.TrustCheesebackend.entity.Affinage;
import ma.trustCheese.TrustCheesebackend.entity.ControleQualite;
import ma.trustCheese.TrustCheesebackend.entity.LotProduitFini;
import ma.trustCheese.TrustCheesebackend.entity.Utilisateur;
import ma.trustCheese.TrustCheesebackend.enums.DecisionControleQualite;
import ma.trustCheese.TrustCheesebackend.enums.StatutAffinage;
import ma.trustCheese.TrustCheesebackend.enums.StatutLotProduitFini;
import ma.trustCheese.TrustCheesebackend.repository.AffinageRepository;
import ma.trustCheese.TrustCheesebackend.repository.ControleQualiteRepository;
import ma.trustCheese.TrustCheesebackend.repository.LotProduitFiniRepository;
import ma.trustCheese.TrustCheesebackend.repository.UtilisateurRepository;
import ma.trustCheese.TrustCheesebackend.service.traceability.TraceabilityEventService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ControleQualiteService {

    private final ControleQualiteRepository controleQualiteRepository;
    private final LotProduitFiniRepository lotProduitFiniRepository;
    private final AffinageRepository affinageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final TraceabilityEventService traceabilityEventService;


    /**
     * Effectue un contrôle qualité sur tous les lots
     * appartenant au même affinage.
     */
    public ControleQualiteResponse effectuerControleQualite(
            ControleQualiteRequest request
    ) {

        // ---------------------------------------------------------
        // 1. Récupérer l'utilisateur connecté
        // ---------------------------------------------------------

        Utilisateur utilisateur = getUtilisateurConnecte();


        // ---------------------------------------------------------
        // 2. Vérifier que l'affinage existe
        // ---------------------------------------------------------

        Affinage affinage = affinageRepository.findById(request.getAffinageId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Affinage introuvable avec l'identifiant : "
                                        + request.getAffinageId()
                        )
                );


        // ---------------------------------------------------------
        // 3. Vérifier que l'affinage est terminé
        // ---------------------------------------------------------

        if (affinage.getStatut() != StatutAffinage.TERMINE) {
            throw new RuntimeException(
                    "Le contrôle qualité ne peut être effectué que sur un affinage terminé"
            );
        }


        // ---------------------------------------------------------
        // 4. Récupérer tous les lots de cet affinage
        // ---------------------------------------------------------

        List<LotProduitFini> lots =
                lotProduitFiniRepository.findByAffinageId(affinage.getId());


        if (lots.isEmpty()) {
            throw new RuntimeException(
                    "Aucun lot n'est associé à cet affinage"
            );
        }


        // ---------------------------------------------------------
        // 5. Vérifier que tous les lots sont prêts pour le contrôle
        // ---------------------------------------------------------

        boolean lotNonPret = lots.stream()
                .anyMatch(lot ->
                        lot.getStatut() != StatutLotProduitFini.EN_AFFINAGE
                );

        if (lotNonPret) {
            throw new RuntimeException(
                    "Tous les lots de l'affinage doivent être dans l'état EN_AFFINAGE"
            );
        }


        // ---------------------------------------------------------
        // 6. Vérifier qu'un contrôle n'a pas déjà été effectué
        //    pour ces lots
        // ---------------------------------------------------------

        for (LotProduitFini lot : lots) {

            List<ControleQualite> controlesExistants =
                    controleQualiteRepository.findByLotProduitFiniId(lot.getId());

            if (!controlesExistants.isEmpty()) {
                throw new RuntimeException(
                        "Le lot " + lot.getNumeroLot()
                                + " possède déjà un contrôle qualité"
                );
            }
        }


        // ---------------------------------------------------------
        // 7. Date commune du contrôle
        // ---------------------------------------------------------

        LocalDateTime dateControle = LocalDateTime.now();


        // ---------------------------------------------------------
        // 8. Créer un contrôle qualité pour chaque lot
        // ---------------------------------------------------------

        List<ControleQualite> controles = lots.stream()
                .map(lot -> ControleQualite.builder()
                        .numeroControle(generateNumeroControle())
                        .dateControle(dateControle)
                        .temperature(request.getTemperature())
                        .ph(request.getPh())
                        .extraitSec(request.getExtraitSec())
                        .texture(request.getTexture())
                        .notes(request.getNotes())
                        .decision(request.getDecision())
                        .utilisateur(utilisateur)
                        .lotProduitFini(lot)
                        .build()
                )
                .toList();


        List<ControleQualite> controlesSauvegardes =
                controleQualiteRepository.saveAll(controles);


        // ---------------------------------------------------------
        // 9. Appliquer la décision à tous les lots
        // ---------------------------------------------------------

        StatutLotProduitFini nouveauStatut =
                request.getDecision() == DecisionControleQualite.LIBERE
                        ? StatutLotProduitFini.LIBERE
                        : StatutLotProduitFini.BLOQUE;


        lots.forEach(lot -> lot.setStatut(nouveauStatut));

        lotProduitFiniRepository.saveAll(lots);
        traceabilityEventService.createQualityControlEvents(
                controlesSauvegardes
        );


        // ---------------------------------------------------------
        // 10. Construire la réponse groupée
        // ---------------------------------------------------------

        return mapToResponse(
                controlesSauvegardes,
                lots,
                affinage,
                utilisateur
        );
    }


    /**
     * Récupère un contrôle qualité par son identifiant.
     */
    @Transactional
    public ControleQualite getControleById(Long id) {

        return controleQualiteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Contrôle qualité introuvable avec l'identifiant : " + id
                        )
                );
    }


    /**
     * Récupère tous les contrôles qualité.
     */
        @Transactional
        public List<ControleQualiteResponse> getAllControles() {

                return controleQualiteRepository.findAll()
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
    }


    /**
     * Récupère les contrôles effectués sur un lot.
     */
        @Transactional
        public List<ControleQualiteResponse> getControlesByLot(Long lotProduitFiniId) {

        // Vérifier que le lot existe
        lotProduitFiniRepository.findById(lotProduitFiniId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Lot introuvable avec l'identifiant : "
                                        + lotProduitFiniId
                        )
                );

        return controleQualiteRepository
                .findByLotProduitFiniId(lotProduitFiniId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    /**
     * Récupère les contrôles effectués par un utilisateur.
     */
        @Transactional
        public List<ControleQualiteResponse> getControlesByUtilisateur(Long utilisateurId) {

        utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Utilisateur introuvable avec l'identifiant : "
                                        + utilisateurId
                        )
                );

        return controleQualiteRepository.findByUtilisateurId(utilisateurId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    /**
     * Génère le numéro du contrôle qualité.
     *
     * Exemple :
     * CQ-2026-001
     * CQ-2026-002
     */
    private String generateNumeroControle() {

        long sequence =
                controleQualiteRepository.getNextControleQualiteSequence();

        int annee = LocalDateTime.now().getYear();

        return String.format(
                "CQ-%d-%03d",
                annee,
                sequence
        );
    }


    /**
     * Récupère l'utilisateur actuellement authentifié.
     */
    private Utilisateur getUtilisateurConnecte() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "Aucun utilisateur authentifié"
            );
        }

        String email = authentication.getName();

        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Utilisateur connecté introuvable"
                        )
                );
    }


    /**
     * Transforme les contrôles créés pour le groupe
     * en une seule réponse destinée au frontend.
     */
    private ControleQualiteResponse mapToResponse(
            List<ControleQualite> controles,
            List<LotProduitFini> lots,
            Affinage affinage,
            Utilisateur utilisateur
    ) {

        ControleQualite premierControle = controles.get(0);

        return ControleQualiteResponse.builder()
                .id(premierControle.getId())

                /*
                 * Un contrôle est créé par lot.
                 * On retourne donc tous les numéros.
                 */
                .numerosControles(
                        controles.stream()
                                .map(ControleQualite::getNumeroControle)
                                .toList()
                )

                .dateControle(premierControle.getDateControle())
                .temperature(premierControle.getTemperature())
                .ph(premierControle.getPh())
                .extraitSec(premierControle.getExtraitSec())
                .texture(premierControle.getTexture())
                .notes(premierControle.getNotes())
                .decision(premierControle.getDecision())

                .utilisateurId(utilisateur.getId())
                .utilisateurNom(utilisateur.getNom())
                .affinageId(affinage.getId())


                .nombreLots(lots.size())


                .numerosLots(
                        lots.stream()
                                .map(LotProduitFini::getNumeroLot)
                                .toList()
                )

                .build();
    }

        private ControleQualiteResponse mapToResponse(ControleQualite controle) {
                LotProduitFini lot = controle.getLotProduitFini();
                Affinage affinage = lot.getAffinage();
                Utilisateur utilisateur = controle.getUtilisateur();

                return ControleQualiteResponse.builder()
                                .id(controle.getId())
                                .numerosControles(List.of(controle.getNumeroControle()))
                                .dateControle(controle.getDateControle())
                                .temperature(controle.getTemperature())
                                .ph(controle.getPh())
                                .extraitSec(controle.getExtraitSec())
                                .texture(controle.getTexture())
                                .notes(controle.getNotes())
                                .decision(controle.getDecision())
                                .utilisateurId(utilisateur.getId())
                                .utilisateurNom(utilisateur.getNom())
                                .affinageId(affinage.getId())
                                .nombreLots(1)
                                .numerosLots(List.of(lot.getNumeroLot()))
                                .build();
        }
}

