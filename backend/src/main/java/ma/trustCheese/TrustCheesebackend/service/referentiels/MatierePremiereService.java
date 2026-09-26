package ma.trustCheese.TrustCheesebackend.service.referentiels;



import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.MatierePremiereRequest;
import ma.trustCheese.TrustCheesebackend.dto.MatierePremiereResponse;
import ma.trustCheese.TrustCheesebackend.entity.MatierePremiere;
import ma.trustCheese.TrustCheesebackend.enums.UniteMesure;
import ma.trustCheese.TrustCheesebackend.repository.MatierePremiereRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatierePremiereService {

    private final MatierePremiereRepository matierePremiereRepository;

    // ─── GET ALL ────────────────────────────────────────────────────────────
    public List<MatierePremiereResponse> getAll() {
        return matierePremiereRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── GET BY ID ──────────────────────────────────────────────────────────
    public MatierePremiereResponse getById(Long id) {
        MatierePremiere mp = findOrThrow(id);
        return toResponse(mp);
    }

    // ─── GET BY UNITE MESURE ─────────────────────────────────────────────────
    public List<MatierePremiereResponse> getByUniteMesure(UniteMesure uniteMesure) {
        return matierePremiereRepository.findByUniteMesure(uniteMesure)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── SEARCH ─────────────────────────────────────────────────────────────
    public List<MatierePremiereResponse> search(String keyword) {
        return matierePremiereRepository.search(keyword)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── CREATE ─────────────────────────────────────────────────────────────
    @Transactional
    public MatierePremiereResponse create(MatierePremiereRequest request) {
        if (matierePremiereRepository.existsByNom(request.getNom())) {
            throw new RuntimeException(
                    "Matière première déjà existante avec le nom : " + request.getNom()
            );
        }

        MatierePremiere mp = MatierePremiere.builder()
                .nom(request.getNom())
                .code(request.getCode())
                .description(request.getDescription())
                .uniteMesure(request.getUniteMesure())
                .seuilStock(request.getSeuilStock())
                .build();

        return toResponse(matierePremiereRepository.save(mp));
    }

    // ─── UPDATE ─────────────────────────────────────────────────────────────
    @Transactional
    public MatierePremiereResponse update(Long id, MatierePremiereRequest request) {
        MatierePremiere mp = findOrThrow(id);

        // Vérifier unicité du nom si modifié
        if (!mp.getNom().equals(request.getNom()) &&
                matierePremiereRepository.existsByNom(request.getNom())) {
            throw new RuntimeException(
                    "Matière première déjà existante avec le nom : " + request.getNom()
            );
        }

        mp.setNom(request.getNom());
        mp.setCode(request.getCode());
        mp.setDescription(request.getDescription());
        mp.setUniteMesure(request.getUniteMesure());
        mp.setSeuilStock(request.getSeuilStock());

        return toResponse(matierePremiereRepository.save(mp));
    }

    // ─── DELETE ─────────────────────────────────────────────────────────────
    @Transactional
    public void delete(Long id) {
        if (!matierePremiereRepository.existsById(id)) {
            throw new RuntimeException(
                    "Matière première non trouvée avec l'id : " + id
            );
        }
        matierePremiereRepository.deleteById(id);
    }

    // ─── HELPERS ────────────────────────────────────────────────────────────
    private MatierePremiere findOrThrow(Long id) {
        return matierePremiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Matière première non trouvée avec l'id : " + id
                ));
    }

    private MatierePremiereResponse toResponse(MatierePremiere mp) {
        MatierePremiereResponse response = new MatierePremiereResponse();
        response.setId(mp.getId());
        response.setNom(mp.getNom());
        response.setCode(mp.getCode());
        response.setDescription(mp.getDescription());
        response.setUniteMesure(mp.getUniteMesure());
        response.setSeuilStock(mp.getSeuilStock());
        return response;
    }
}