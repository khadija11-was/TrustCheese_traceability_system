package ma.trustCheese.TrustCheesebackend.service.stock;


import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.LotMPRequest;
import ma.trustCheese.TrustCheesebackend.dto.LotMPResponse;
import ma.trustCheese.TrustCheesebackend.entity.Fournisseur;
import ma.trustCheese.TrustCheesebackend.entity.LotMP;
import ma.trustCheese.TrustCheesebackend.entity.MatierePremiere;
import ma.trustCheese.TrustCheesebackend.enums.EtatStock;
import ma.trustCheese.TrustCheesebackend.repository.FournisseurRepository;
import ma.trustCheese.TrustCheesebackend.repository.LotMPRepository;
import ma.trustCheese.TrustCheesebackend.repository.MatierePremiereRepository;
import ma.trustCheese.TrustCheesebackend.repository.MovementStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LotMPService {

    private final LotMPRepository lotMPRepository;
    private final FournisseurRepository fournisseurRepository;
    private final MatierePremiereRepository matierePremiereRepository;
    private final MovementStockRepository movementStockRepository;


    // =========================
    // CREATE
    // =========================

    public LotMPResponse create(LotMPRequest request) {

        Fournisseur fournisseur =
                fournisseurRepository.findById(request.getFournisseurId())
                        .orElseThrow(() ->
                                new RuntimeException("Fournisseur introuvable"));

        MatierePremiere matierePremiere =
                matierePremiereRepository.findById(request.getMatierePremiereId())
                        .orElseThrow(() ->
                                new RuntimeException("Matière première introuvable"));

        String numeroLot = genererNumeroLot(matierePremiere);

        LotMP lotMP = LotMP.builder()
                .numeroLot(numeroLot)
                .quantite(request.getQuantite())
                .dateReception(request.getDateReception())
                .fournisseur(fournisseur)
                .matierePremiere(matierePremiere)
                .build();

        lotMP = lotMPRepository.save(lotMP);

        return toResponse(lotMP);
    }


    // =========================
    // GET BY ID
    // =========================

    @Transactional(readOnly = true)
    public LotMPResponse getById(Long id) {

        LotMP lotMP = lotMPRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Lot MP introuvable"));

        return toResponse(lotMP);
    }


    // =========================
    // GET ALL
    // =========================

    @Transactional(readOnly = true)
    public List<LotMPResponse> getAll() {

        return lotMPRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    // =========================
    // CALCUL STOCK
    // =========================

    private StockInfo calculerStock(LotMP lotMP) {

        BigDecimal quantiteSortie =
                movementStockRepository.sumQuantiteByLotMP(lotMP.getId());

        BigDecimal quantiteRestante =
                lotMP.getQuantite().subtract(quantiteSortie);

        EtatStock etatStock;

        if (quantiteRestante.compareTo(BigDecimal.ZERO) <= 0) {

            etatStock = EtatStock.EPUISE;

        } else if (
                quantiteRestante.compareTo(lotMP.getQuantite()) < 0
        ) {

            etatStock = EtatStock.PARTIELLEMENT_UTILISE;

        } else {

            etatStock = EtatStock.DISPONIBLE;
        }

        return new StockInfo(
                quantiteRestante,
                etatStock
        );
    }


    // =========================
    // CONVERSION RESPONSE
    // =========================

    private LotMPResponse toResponse(LotMP lotMP) {

        StockInfo stockInfo = calculerStock(lotMP);

        MatierePremiere mp = lotMP.getMatierePremiere();
        Fournisseur fournisseur = lotMP.getFournisseur();

        return LotMPResponse.builder()

                .id(lotMP.getId())
                .numeroLot(lotMP.getNumeroLot())

                .matierePremiereId(mp.getId())
                .matierePremiereNom(mp.getNom())
                .uniteMesure(mp.getUniteMesure())

                .fournisseurId(fournisseur.getId())
                .fournisseurNom(fournisseur.getNom())

                .quantite(lotMP.getQuantite())
                .dateReception(lotMP.getDateReception())

                .quantiteRestante(stockInfo.quantiteRestante())
                .seuilStock(mp.getSeuilStock())
                .etatStock(stockInfo.etatStock())

                .build();
    }


    // =========================
    // GENERATION NUMERO LOT
    // =========================

    private String genererNumeroLot(MatierePremiere matierePremiere) {

        String codeMP = matierePremiere.getCode();
        int annee = LocalDate.now().getYear();

        Long sequence = lotMPRepository.getNextLotNumber();

        return String.format(
                "%s-%d-%03d",
                codeMP,
                annee,
                sequence
        );
    }


    // =========================
    // STOCK INFO
    // =========================

    private record StockInfo(
            BigDecimal quantiteRestante,
            EtatStock etatStock
    ) {}
}