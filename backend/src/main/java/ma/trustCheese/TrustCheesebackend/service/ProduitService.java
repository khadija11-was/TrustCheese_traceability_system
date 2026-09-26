package ma.trustCheese.TrustCheesebackend.service;


import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.ProduitRequest;
import ma.trustCheese.TrustCheesebackend.dto.ProduitResponse;
import ma.trustCheese.TrustCheesebackend.entity.Produit;
import ma.trustCheese.TrustCheesebackend.repository.ProduitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final FileStorageService fileStorageService;

    /**
     * Créer un nouveau produit.
     */
    public ProduitResponse createProduit(ProduitRequest request) {

        // Vérifier si un produit portant le même nom existe déjà
        if (produitRepository.existsByNomIgnoreCase(request.getNom())) {
            throw new IllegalArgumentException(
                    "Un produit avec le nom '" + request.getNom() + "' existe déjà."
            );
        }

        // Créer l'entité Produit
        Produit produit = Produit.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .quantiteStandardLot(request.getQuantiteStandardLot())
                .dureeConservationJours(request.getDureeConservationJours())
                .build();

        // Sauvegarder l'image si elle existe
        MultipartFile image = request.getImage();

        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.storeProductImage(image);
            produit.setImageUrl(imageUrl);
        }

        // Sauvegarder le produit
        Produit savedProduit = produitRepository.save(produit);

        return mapToResponse(savedProduit);
    }

    /**
     * Récupérer tous les produits.
     */
    @Transactional(readOnly = true)
    public List<ProduitResponse> getAllProduits() {

        return produitRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Récupérer un produit par son ID.
     */
    @Transactional(readOnly = true)
    public ProduitResponse getProduitById(Long id) {

        Produit produit = findProduitById(id);

        return mapToResponse(produit);
    }

    /**
     * Modifier un produit.
     */
    public ProduitResponse updateProduit(Long id, ProduitRequest request) {

        Produit produit = findProduitById(id);

        // Vérifier l'unicité du nom uniquement
        // si le nom a réellement changé
        if (!produit.getNom().equalsIgnoreCase(request.getNom())
                && produitRepository.existsByNomIgnoreCase(request.getNom())) {

            throw new IllegalArgumentException(
                    "Un produit avec le nom '" + request.getNom() + "' existe déjà."
            );
        }

        // Mise à jour des informations
        produit.setNom(request.getNom());
        produit.setDescription(request.getDescription());
        produit.setQuantiteStandardLot(request.getQuantiteStandardLot());
        produit.setDureeConservationJours(request.getDureeConservationJours());

        // Nouvelle image
        MultipartFile newImage = request.getImage();

        if (newImage != null && !newImage.isEmpty()) {

            // Sauvegarder la nouvelle image
            String newImageUrl =
                    fileStorageService.storeProductImage(newImage);

            // Supprimer l'ancienne image
            if (produit.getImageUrl() != null
                    && !produit.getImageUrl().isBlank()) {

                fileStorageService.deleteProductImage(produit.getImageUrl());
            }

            // Associer la nouvelle image au produit
            produit.setImageUrl(newImageUrl);
        }

        Produit updatedProduit = produitRepository.save(produit);

        return mapToResponse(updatedProduit);
    }

    /**
     * Supprimer un produit.
     */
    public void deleteProduit(Long id) {

        Produit produit = findProduitById(id);

        // Supprimer l'image associée au produit
        if (produit.getImageUrl() != null
                && !produit.getImageUrl().isBlank()) {

            fileStorageService.deleteProductImage(produit.getImageUrl());
        }

        // Supprimer le produit de la base de données
        produitRepository.delete(produit);
    }

    /**
     * Recherche interne d'un produit.
     */
    private Produit findProduitById(Long id) {

        return produitRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Produit introuvable avec l'id : " + id
                        )
                );
    }

    /**
     * Conversion Entity -> Response DTO.
     */
    private ProduitResponse mapToResponse(Produit produit) {

        return ProduitResponse.builder()
                .id(produit.getId())
                .nom(produit.getNom())
                .description(produit.getDescription())
                .quantiteStandardLot(produit.getQuantiteStandardLot())
                .dureeConservationJours(produit.getDureeConservationJours())
                .imageUrl(produit.getImageUrl())
                .build();
    }
}
