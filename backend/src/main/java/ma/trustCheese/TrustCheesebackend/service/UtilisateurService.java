package ma.trustCheese.TrustCheesebackend.service;


import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.UtilisateurRequest;
import ma.trustCheese.TrustCheesebackend.dto.UtilisateurResponse;
import ma.trustCheese.TrustCheesebackend.entity.Utilisateur;
import ma.trustCheese.TrustCheesebackend.enums.Role;
import ma.trustCheese.TrustCheesebackend.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Créer un nouvel utilisateur.
     */
    public UtilisateurResponse createUtilisateur(UtilisateurRequest request) {

        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException(
                    "Un utilisateur existe déjà avec l'email : " + request.getEmail()
            );
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .nom(request.getNom())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(request.getRole())
                .actif(true)
                .build();

        Utilisateur utilisateurSauvegarde =
                utilisateurRepository.save(utilisateur);

        return mapToResponse(utilisateurSauvegarde);
    }

    /**
     * Récupérer tous les utilisateurs.
     */
    @Transactional(readOnly = true)
    public List<UtilisateurResponse> getAllUtilisateurs() {

        return utilisateurRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Récupérer un utilisateur par son identifiant.
     */
    @Transactional(readOnly = true)
    public UtilisateurResponse getUtilisateurById(Long id) {

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Utilisateur introuvable avec l'identifiant : " + id
                        )
                );

        return mapToResponse(utilisateur);
    }

    /**
     * Modifier les informations d'un utilisateur.
     */
    public UtilisateurResponse updateUtilisateur(
            Long id,
            UtilisateurRequest request) {

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Utilisateur introuvable avec l'identifiant : " + id
                        )
                );

        if (!utilisateur.getEmail().equals(request.getEmail())
                && utilisateurRepository.existsByEmail(request.getEmail())) {

            throw new RuntimeException(
                    "Un utilisateur existe déjà avec l'email : "
                            + request.getEmail()
            );
        }

        utilisateur.setNom(request.getNom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setRole(request.getRole());

        /*
         * On ne remplace le mot de passe que si
         * l'administrateur en fournit un nouveau.
         */
        if (request.getMotDePasse() != null
                && !request.getMotDePasse().isBlank()) {

            utilisateur.setMotDePasse(
                    passwordEncoder.encode(request.getMotDePasse())
            );
        }

        return mapToResponse(
                utilisateurRepository.save(utilisateur)
        );
    }

    /**
     * Activer un utilisateur.
     */
    public UtilisateurResponse activerUtilisateur(Long id) {

        Utilisateur utilisateur = findUtilisateur(id);

        utilisateur.setActif(true);

        return mapToResponse(
                utilisateurRepository.save(utilisateur)
        );
    }

    /**
     * Désactiver un utilisateur.
     */
    public UtilisateurResponse desactiverUtilisateur(Long id) {

        Utilisateur utilisateur = findUtilisateur(id);

        utilisateur.setActif(false);

        return mapToResponse(
                utilisateurRepository.save(utilisateur)
        );
    }

    /**
     * Rechercher les utilisateurs par nom.
     */
    @Transactional(readOnly = true)
    public List<UtilisateurResponse> rechercherParNom(String nom) {

        return utilisateurRepository
                .findByNomContainingIgnoreCase(nom)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Filtrer les utilisateurs par rôle.
     */
    @Transactional(readOnly = true)
    public List<UtilisateurResponse> getUtilisateursByRole(Role role) {

        return utilisateurRepository
                .findByRole(role)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Recherche interne d'un utilisateur.
     */
    private Utilisateur findUtilisateur(Long id) {

        return utilisateurRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Utilisateur introuvable avec l'identifiant : " + id
                        )
                );
    }

    /**
     * Transformer l'entité en réponse API.
     */
    private UtilisateurResponse mapToResponse(
            Utilisateur utilisateur) {

        return UtilisateurResponse.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole())
                .actif(utilisateur.isActif())
                .dateCreation(utilisateur.getDateCreation())
                .dernierConnexion(utilisateur.getDernierConnexion())
                .build();
    }
}