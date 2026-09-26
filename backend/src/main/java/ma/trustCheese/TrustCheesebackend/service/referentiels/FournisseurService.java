package ma.trustCheese.TrustCheesebackend.service.referentiels;


import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.FournisseurRequest;
import ma.trustCheese.TrustCheesebackend.dto.FournisseurResponse;
import ma.trustCheese.TrustCheesebackend.entity.Fournisseur;
import ma.trustCheese.TrustCheesebackend.repository.FournisseurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;

    // ─── GET ALL ────────────────────────────────────────────────────────────
    public List<FournisseurResponse> getAll() {
        return fournisseurRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── GET BY ID ──────────────────────────────────────────────────────────
    public FournisseurResponse getById(Long id) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec l'id : " + id));
        return toResponse(fournisseur);
    }

    // ─── GET BY CODE ────────────────────────────────────────────────────────
    public FournisseurResponse getByCode(String code) {
        Fournisseur fournisseur = fournisseurRepository.findByCodeFournisseur(code)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec le code : " + code));
        return toResponse(fournisseur);
    }

    // ─── SEARCH ─────────────────────────────────────────────────────────────
    public List<FournisseurResponse> search(String keyword) {
        return fournisseurRepository.search(keyword)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── CREATE ─────────────────────────────────────────────────────────────
    @Transactional
    public FournisseurResponse create(FournisseurRequest request) {

        Long numero= fournisseurRepository.getNextCodeNumber();
        String codeFournisseur =
                String.format("FOUR-%05d", numero);

        Fournisseur fournisseur = Fournisseur.builder()
                .codeFournisseur(codeFournisseur)
                .nom(request.getNom())
                .email(request.getEmail())
                .telephone(request.getTelephone())
                .adresse(request.getAdresse())
                .build();
        fournisseur= fournisseurRepository.save(fournisseur);
        return FournisseurResponse.builder()
                .id(fournisseur.getId())
                .codeFournisseur(fournisseur.getCodeFournisseur())
                .nom(fournisseur.getNom())
                .email(fournisseur.getEmail())
                .telephone(fournisseur.getTelephone())
                .adresse(fournisseur.getAdresse())
                .build();
    }

    // ─── UPDATE ─────────────────────────────────────────────────────────────
    @Transactional
    public FournisseurResponse update(Long id, FournisseurRequest request) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec l'id : " + id));


        // Vérifier unicité de l'email si modifié
        if (request.getEmail() != null &&
                !request.getEmail().equals(fournisseur.getEmail()) &&
                fournisseurRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé : " + request.getEmail());
        }

        fournisseur.setNom(request.getNom());
        fournisseur.setEmail(request.getEmail());
        fournisseur.setTelephone(request.getTelephone());
        fournisseur.setAdresse(request.getAdresse());

        return toResponse(fournisseurRepository.save(fournisseur));
    }

    // ─── DELETE ─────────────────────────────────────────────────────────────
    @Transactional
    public void delete(Long id) {
        if (!fournisseurRepository.existsById(id)) {
            throw new RuntimeException("Fournisseur non trouvé avec l'id : " + id);
        }
        fournisseurRepository.deleteById(id);
    }

    // ─── MAPPERS ────────────────────────────────────────────────────────────
    private FournisseurResponse toResponse(Fournisseur f) {
        return FournisseurResponse.builder()
                .id(f.getId())
                .codeFournisseur(f.getCodeFournisseur())
                .nom(f.getNom())
                .email(f.getEmail())
                .telephone(f.getTelephone())
                .adresse(f.getAdresse())
                .build();
    }

    private Fournisseur toEntity(FournisseurRequest request) {
        return Fournisseur.builder()
                .nom(request.getNom())
                .email(request.getEmail())
                .telephone(request.getTelephone())
                .adresse(request.getAdresse())
                .build();
    }
}
