package ma.trustCheese.TrustCheesebackend.service;


import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.entity.LotProduitFini;
import ma.trustCheese.TrustCheesebackend.entity.Production;
import ma.trustCheese.TrustCheesebackend.entity.Produit;
import ma.trustCheese.TrustCheesebackend.enums.StatutLotProduitFini;
import ma.trustCheese.TrustCheesebackend.repository.LotProduitFiniRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LotProduitFiniService {

    private final LotProduitFiniRepository lotProduitFiniRepository;

    /**
     * Génère les lots de produits finis à la fin d'une production.
     *
     * Chaque lot reçoit :
     * - un numéro unique généré par PostgreSQL
     * - une date de mouvement
     * - une date d'expiration calculée à partir du produit
     * - le statut DISPONIBLE
     * - la production à laquelle il appartient
     */
    public List<LotProduitFini> generateLots(
            Production production,
            int nombreLots
    ) {

        if (production == null) {
            throw new IllegalArgumentException(
                    "La production est obligatoire"
            );
        }

        if (nombreLots <= 0) {
            throw new IllegalArgumentException(
                    "Le nombre de lots doit être supérieur à zéro"
            );
        }

        if (production.getDateFin() == null) {
            throw new IllegalArgumentException(
                    "La date de fin réelle de la production est obligatoire"
            );
        }

        Produit produit = production.getProduit();

        if (produit == null) {
            throw new IllegalArgumentException(
                    "Le produit associé à la production est obligatoire"
            );
        }

        if (produit.getDureeConservationJours() == null
                || produit.getDureeConservationJours() <= 0) {

            throw new IllegalArgumentException(
                    "La durée de conservation du produit doit être définie"
            );
        }

        List<LotProduitFini> lots = new java.util.ArrayList<>();

        LocalDate dateExpiration = calculateDateExpiration(produit, production);

        for (int i = 0; i < nombreLots; i++) {

            String numeroLot = generateNumeroLot();

            LotProduitFini lot = LotProduitFini.builder()
                    .numeroLot(numeroLot)
                    .dateExpiration(dateExpiration)
                    .statut(StatutLotProduitFini.DISPONIBLE)
                    .production(production)
                    .build();

            lots.add(lot);
        }

        return lotProduitFiniRepository.saveAll(lots);
    }

    /**
     * Génère un numéro de lot unique.
     *
     * Exemple :
     * LOT-000001
     * LOT-000002
     * LOT-000003
     */
    private String generateNumeroLot() {

        long sequence = lotProduitFiniRepository.getNextLotSequence();

        return String.format("LOT-%06d", sequence);
    }

    /**
     * Calcule la date d'expiration du lot.
     *
     * La référence est la date de fin réelle de la production.
     *
     * Exemple :
     * Date de production : 20/09/2026
     * Durée de conservation : 90 jours
     *
     * Date d'expiration : 19/12/2026
     */
    private LocalDate calculateDateExpiration(
            Produit produit,
            Production production
    ) {

        LocalDate dateProduction =
                production.getDateFin().toLocalDate();

        return dateProduction.plusDays(
                produit.getDureeConservationJours()
        );
    }

    /**
     * Récupère tous les lots d'une production.
     */
    @Transactional(readOnly = true)
    public List<LotProduitFini> getLotsByProduction(
            Long productionId
    ) {

        return lotProduitFiniRepository
                .findByProductionId(productionId);
    }

    /**
     * Récupère un lot par son identifiant.
     */
    @Transactional(readOnly = true)
    public LotProduitFini getLotById(Long id) {

        return lotProduitFiniRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Lot introuvable : " + id
                        )
                );
    }

    /**
     * Récupère un lot par son numéro.
     */
    @Transactional(readOnly = true)
    public LotProduitFini getLotByNumero(
            String numeroLot
    ) {

        return lotProduitFiniRepository
                .findByNumeroLot(numeroLot)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Lot introuvable : " + numeroLot
                        )
                );
    }

    /**
     * Récupère tous les lots.
     */
    @Transactional(readOnly = true)
    public List<LotProduitFini> getAllLots() {

        return lotProduitFiniRepository.findAll();
    }
}
