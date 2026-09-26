package ma.trustCheese.TrustCheesebackend.service.livraison;



import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.livraison.LigneLivraisonRequest;
import ma.trustCheese.TrustCheesebackend.dto.livraison.LigneLivraisonResponse;
import ma.trustCheese.TrustCheesebackend.dto.livraison.LivraisonRequest;
import ma.trustCheese.TrustCheesebackend.dto.livraison.LivraisonResponse;
import ma.trustCheese.TrustCheesebackend.entity.Client;
import ma.trustCheese.TrustCheesebackend.entity.LigneLivraison;
import ma.trustCheese.TrustCheesebackend.entity.Livraison;
import ma.trustCheese.TrustCheesebackend.entity.LotProduitFini;
import ma.trustCheese.TrustCheesebackend.entity.Produit;
import ma.trustCheese.TrustCheesebackend.entity.Utilisateur;
import ma.trustCheese.TrustCheesebackend.enums.StatutLivraison;
import ma.trustCheese.TrustCheesebackend.enums.StatutLotProduitFini;
import ma.trustCheese.TrustCheesebackend.repository.ClientRepository;
import ma.trustCheese.TrustCheesebackend.repository.LivraisonRepository;
import ma.trustCheese.TrustCheesebackend.repository.LotProduitFiniRepository;
import ma.trustCheese.TrustCheesebackend.repository.ProduitRepository;
import ma.trustCheese.TrustCheesebackend.repository.UtilisateurRepository;
import ma.trustCheese.TrustCheesebackend.service.TraceabilityEventService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LivraisonService {

    private final LivraisonRepository livraisonRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final LotProduitFiniRepository lotProduitFiniRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final TraceabilityEventService traceabilityEventService;


    // =========================================================
    // CREATION
    // =========================================================

    public LivraisonResponse createLivraison(
            LivraisonRequest request
    ) {

        // -----------------------------------------------------
        // 1. Vérifier le client
        // -----------------------------------------------------

        Client client = clientRepository
                .findById(request.getClientId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Client introuvable."
                        )
                );

        if (!Boolean.TRUE.equals(client.getActif())) {
            throw new IllegalArgumentException(
                    "Le client sélectionné est inactif."
            );
        }


        // -----------------------------------------------------
        // 2. Vérifier les lignes
        // -----------------------------------------------------

        if (request.getLignes() == null
                || request.getLignes().isEmpty()) {

            throw new IllegalArgumentException(
                    "La livraison doit contenir au moins une ligne."
            );
        }


        // -----------------------------------------------------
        // 3. Utilisateur connecté
        // -----------------------------------------------------

        Utilisateur responsable = getAuthenticatedUser();


        // -----------------------------------------------------
        // 4. Créer la livraison
        // -----------------------------------------------------

        Livraison livraison = Livraison.builder()
                .numeroLivraison(generateNumeroLivraison())
                .client(client)
                .dateLivraisonPrevue(
                        request.getDateLivraisonPrevue()
                )
                .statut(StatutLivraison.PLANIFIEE)
                .adresseLivraison(
                        request.getAdresseLivraison()
                )
                .ville(request.getVille())
                .pays(request.getPays())
                .temperatureMoyenneCamion(request.getTemperatureMoyenneCamion())
                .responsable(responsable)
                .build();


        // -----------------------------------------------------
        // 5. Créer les lignes
        // -----------------------------------------------------

        List<LigneLivraison> lignes = new ArrayList<>();

        for (LigneLivraisonRequest ligneRequest
                : request.getLignes()) {

            if (ligneRequest.getQuantiteLots() == null
                    || ligneRequest.getQuantiteLots() <= 0) {

                throw new IllegalArgumentException(
                        "La quantité de lots doit être supérieure à zéro."
                );
            }

            Produit produit = produitRepository
                    .findById(ligneRequest.getProduitId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Produit introuvable : "
                                            + ligneRequest.getProduitId()
                            )
                    );

            LigneLivraison ligne = LigneLivraison.builder()
                    .livraison(livraison)
                    .produit(produit)
                    .quantiteLots(
                            ligneRequest.getQuantiteLots()
                    )

                    .build();

            lignes.add(ligne);
        }

        livraison.setLignes(lignes);


        // -----------------------------------------------------
        // 6. Sauvegarder
        // -----------------------------------------------------

        Livraison savedLivraison =
                livraisonRepository.save(livraison);

        return mapToResponse(savedLivraison);
    }


    // =========================================================
    // PREPARATION
    // =========================================================

    public LivraisonResponse preparerLivraison(
            Long livraisonId
    ) {

        Livraison livraison = getLivraison(livraisonId);


        // -----------------------------------------------------
        // Vérifier le statut
        // -----------------------------------------------------

        if (livraison.getStatut()
                != StatutLivraison.PLANIFIEE) {

            throw new IllegalStateException(
                    "La livraison doit être planifiée "
                            + "pour commencer sa préparation."
            );
        }


        // -----------------------------------------------------
        // Pour chaque ligne :
        // sélectionner les lots FIFO
        // -----------------------------------------------------

        for (LigneLivraison ligne
                : livraison.getLignes()) {

            Produit produit = ligne.getProduit();

            Integer quantiteDemandee =
                    ligne.getQuantiteLots();


            // -----------------------------------------------
            // Récupérer les lots LIBERE
            // dans l'ordre FIFO
            // -----------------------------------------------

            List<LotProduitFini> lotsDisponibles =
                    lotProduitFiniRepository
                            .findByProductionProduitIdAndStatutOrderByProductionDateFinAsc(
                                    produit.getId(),
                                    StatutLotProduitFini.LIBERE
                            );


            // -----------------------------------------------
            // Vérifier la quantité disponible
            // -----------------------------------------------

            if (lotsDisponibles.size()
                    < quantiteDemandee) {

                throw new IllegalArgumentException(
                        "Quantité insuffisante pour le produit "
                                + produit.getNom()
                                + ". Demandé : "
                                + quantiteDemandee
                                + ", disponible : "
                                + lotsDisponibles.size()
                );
            }


            // -----------------------------------------------
            // Sélection FIFO
            // -----------------------------------------------

            List<LotProduitFini> lotsSelectionnes =
                    new ArrayList<>(
                            lotsDisponibles.subList(
                                    0,
                                    quantiteDemandee
                            )
                    );


            // -----------------------------------------------
            // Réserver les lots
            // -----------------------------------------------

            for (LotProduitFini lot
                    : lotsSelectionnes) {

                lot.setStatut(
                        StatutLotProduitFini.RESERVE
                );
            }

            lotProduitFiniRepository.saveAll(
                    lotsSelectionnes
            );


            // -----------------------------------------------
            // Associer les lots à la ligne
            // -----------------------------------------------

            ligne.setLotsProduitFini(
                    lotsSelectionnes
            );
        }


        // -----------------------------------------------------
        // Changer le statut
        // -----------------------------------------------------

        livraison.setStatut(
                StatutLivraison.EN_PREPARATION
        );

        Livraison savedLivraison =
                livraisonRepository.save(livraison);

        return mapToResponse(savedLivraison);
    }


    // =========================================================
    // EXPEDITION
    // =========================================================

    public LivraisonResponse expedierLivraison(
            Long livraisonId
    ) {

        Livraison livraison = getLivraison(livraisonId);


        if (livraison.getStatut()
                != StatutLivraison.EN_PREPARATION) {

            throw new IllegalStateException(
                    "La livraison doit être en préparation "
                            + "avant d'être expédiée."
            );
        }


        livraison.setStatut(
                StatutLivraison.EXPEDIEE
        );

        Livraison savedLivraison =
                livraisonRepository.save(livraison);

        return mapToResponse(savedLivraison);
    }


    // =========================================================
    // CONFIRMATION DE LIVRAISON
    // =========================================================

    public LivraisonResponse confirmerLivraison(
            Long livraisonId
    ) {

        Livraison livraison = getLivraison(livraisonId);


        // -----------------------------------------------------
        // Vérifier le statut
        // -----------------------------------------------------

        if (livraison.getStatut()
                != StatutLivraison.EXPEDIEE) {

            throw new IllegalStateException(
                    "La livraison doit être expédiée "
                            + "avant d'être confirmée."
            );
        }


        // -----------------------------------------------------
        // Utilisateur qui confirme
        // -----------------------------------------------------

        getAuthenticatedUser();


        // -----------------------------------------------------
        // Date réelle
        // -----------------------------------------------------

        livraison.setDateLivraisonReelle(
                LocalDateTime.now()
        );


        // -----------------------------------------------------
        // Passer les lots RESERVE → LIVRE
        // -----------------------------------------------------

        List<LotProduitFini> lotsLivres =
                new ArrayList<>();

        for (LigneLivraison ligne
                : livraison.getLignes()) {

            for (LotProduitFini lot
                    : ligne.getLotsProduitFini()) {

                if (lot.getStatut()
                        != StatutLotProduitFini.RESERVE) {

                    throw new IllegalStateException(
                            "Le lot "
                                    + lot.getNumeroLot()
                                    + " n'est pas réservé."
                    );
                }

                lot.setStatut(
                        StatutLotProduitFini.LIVRE
                );

                lotsLivres.add(lot);
            }
        }


        lotProduitFiniRepository.saveAll(
                lotsLivres
        );


        // -----------------------------------------------------
        // Statut livraison
        // -----------------------------------------------------

        livraison.setStatut(
                StatutLivraison.LIVREE
        );


        Livraison savedLivraison =
                livraisonRepository.save(livraison);


        // -----------------------------------------------------
        // Création des événements de traçabilité
        // -----------------------------------------------------

        traceabilityEventService.createDeliveryEvents(
                savedLivraison
        );


        return mapToResponse(savedLivraison);
    }


    // =========================================================
    // ANNULATION
    // =========================================================

    public LivraisonResponse annulerLivraison(
            Long livraisonId
    ) {

        Livraison livraison = getLivraison(livraisonId);


        // -----------------------------------------------------
        // Vérifier si l'annulation est autorisée
        // -----------------------------------------------------

        if (livraison.getStatut()
                != StatutLivraison.PLANIFIEE
                && livraison.getStatut()
                != StatutLivraison.EN_PREPARATION) {

            throw new IllegalStateException(
                    "Cette livraison ne peut plus être annulée."
            );
        }


        // -----------------------------------------------------
        // Si préparation déjà effectuée :
        // RESERVE → LIBERE
        // -----------------------------------------------------

        if (livraison.getStatut()
                == StatutLivraison.EN_PREPARATION) {

            List<LotProduitFini> lotsAliberer =
                    new ArrayList<>();

            for (LigneLivraison ligne
                    : livraison.getLignes()) {

                for (LotProduitFini lot
                        : ligne.getLotsProduitFini()) {

                    if (lot.getStatut()
                            == StatutLotProduitFini.RESERVE) {

                        lot.setStatut(
                                StatutLotProduitFini.LIBERE
                        );

                        lotsAliberer.add(lot);
                    }
                }

                ligne.getLotsProduitFini().clear();
            }

            lotProduitFiniRepository.saveAll(
                    lotsAliberer
            );
        }


        // -----------------------------------------------------
        // Annuler la livraison
        // -----------------------------------------------------

        livraison.setStatut(
                StatutLivraison.ANNULEE
        );

        Livraison savedLivraison =
                livraisonRepository.save(livraison);

        return mapToResponse(savedLivraison);
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @Transactional
    public LivraisonResponse getLivraisonById(
            Long livraisonId
    ) {

        Livraison livraison = getLivraison(livraisonId);

        return mapToResponse(livraison);
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @Transactional
    public List<LivraisonResponse> getAllLivraisons() {

        return livraisonRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET BY CLIENT
    // =========================================================

    @Transactional
    public List<LivraisonResponse> getLivraisonsByClient(
            Long clientId
    ) {

        return livraisonRepository
                .findByClientIdOrderByDateLivraisonPrevueAsc(
                        clientId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET BY STATUS
    // =========================================================

    @Transactional
    public List<LivraisonResponse> getLivraisonsByStatut(
            StatutLivraison statut
    ) {

        return livraisonRepository
                .findByStatutOrderByDateLivraisonPrevueAsc(
                        statut
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // PRIVATE METHODS
    // =========================================================

    private Livraison getLivraison(
            Long livraisonId
    ) {

        return livraisonRepository
                .findById(livraisonId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Livraison introuvable : "
                                        + livraisonId
                        )
                );
    }


    private Utilisateur getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(
                authentication.getPrincipal()
        )) {

            throw new IllegalStateException(
                    "Aucun utilisateur authentifié."
            );
        }

        String email =
                authentication.getName();

        return utilisateurRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Utilisateur connecté introuvable."
                        )
                );
    }


    private String generateNumeroLivraison() {

        String date =
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter
                                        .ofPattern("yyyyMMdd")
                        );

        String suffix =
                UUID.randomUUID()
                        .toString()
                        .substring(0, 6)
                        .toUpperCase();

        return "LIV-" + date + "-" + suffix;
    }


    private LivraisonResponse mapToResponse(
            Livraison livraison
    ) {

        List<LigneLivraisonResponse> lignes =
                livraison.getLignes()
                        .stream()
                        .map(this::mapLigneToResponse)
                        .toList();

        return LivraisonResponse.builder()
                .id(livraison.getId())
                .numeroLivraison(
                        livraison.getNumeroLivraison()
                )
                .clientId(
                        livraison.getClient().getId()
                )
                .clientNom(
                        livraison.getClient().getNom()
                )
                .dateLivraisonPrevue(
                        livraison.getDateLivraisonPrevue()
                )
                .dateLivraisonReelle(
                        livraison.getDateLivraisonReelle()
                )
                .statut(
                        livraison.getStatut()
                )
                .adresseLivraison(
                        livraison.getAdresseLivraison()
                )
                .ville(
                        livraison.getVille()
                )
                .pays(
                        livraison.getPays()
                )
                .temperatureMoyenneCamion(
                        livraison.getTemperatureMoyenneCamion()
                )
                .responsableId(
                        livraison.getResponsable() != null
                                ? livraison.getResponsable().getId()
                                : null
                )
                .responsableNom(
                        livraison.getResponsable() != null
                                ? livraison.getResponsable().getNom()
                                : null
                )
                .lignes(lignes)

                .build();
    }


    private LigneLivraisonResponse mapLigneToResponse(
            LigneLivraison ligne
    ) {

        List<LigneLivraisonResponse.LotInfo> lots =
                ligne.getLotsProduitFini()
                        .stream()
                        .map(lot ->
                                LigneLivraisonResponse.LotInfo
                                        .builder()
                                        .id(lot.getId())
                                        .numeroLot(
                                                lot.getNumeroLot()
                                        )
                                        .dateExpiration(
                                                lot.getDateExpiration()
                                        )
                                        .statut(
                                                lot.getStatut().name()
                                        )
                                        .build()
                        )
                        .toList();

        return LigneLivraisonResponse.builder()
                .id(ligne.getId())
                .produitId(
                        ligne.getProduit().getId()
                )
                .produitNom(
                        ligne.getProduit().getNom()
                )
                .quantiteLots(
                        ligne.getQuantiteLots()
                )

                .build();
    }
}