package ma.trustCheese.TrustCheesebackend.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path productsDirectory;

    // Types d'images autorisés
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    // Taille maximale : 5 MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    public FileStorageService(
            @Value("${app.upload.products-dir}") String productsDirectory
    ) {
        this.productsDirectory = Paths.get(productsDirectory)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.productsDirectory);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Impossible de créer le dossier de stockage des images.",
                    e
            );
        }
    }

    /**
     * Sauvegarde une image de produit.
     *
     * @param file image envoyée par le frontend
     * @return chemin relatif de l'image sauvegardée
     */
    public String storeProductImage(MultipartFile file) {

        validateImage(file);

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null
                        ? file.getOriginalFilename()
                        : ""
        );

        String extension = getExtension(originalFilename);

        String filename = UUID.randomUUID() + extension;

        Path targetLocation = productsDirectory.resolve(filename)
                .normalize();

        // Sécurité : empêcher une éventuelle sortie du dossier
        if (!targetLocation.startsWith(productsDirectory)) {
            throw new RuntimeException(
                    "Chemin de fichier invalide."
            );
        }

        try (InputStream inputStream = file.getInputStream()) {

            Files.copy(
                    inputStream,
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Impossible de sauvegarder l'image du produit.",
                    e
            );
        }

        return "/uploads/products/" + filename;
    }

    /**
     * Supprime une image de produit existante.
     *
     * @param imageUrl URL relative enregistrée dans la base
     */
    public void deleteProductImage(String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String prefix = "/uploads/products/";

        if (!imageUrl.startsWith(prefix)) {
            return;
        }

        String filename = imageUrl.substring(prefix.length());

        Path filePath = productsDirectory.resolve(filename)
                .normalize();

        // Sécurité : empêcher de sortir du dossier products
        if (!filePath.startsWith(productsDirectory)) {
            throw new RuntimeException(
                    "Chemin de fichier invalide."
            );
        }

        try {
            Files.deleteIfExists(filePath);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Impossible de supprimer l'image du produit.",
                    e
            );
        }
    }

    /**
     * Vérifie que le fichier envoyé est une image valide.
     */
    private void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "L'image du produit est vide."
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "L'image du produit ne doit pas dépasser 5 MB."
            );
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {

            throw new IllegalArgumentException(
                    "Format d'image non supporté. " +
                            "Formats autorisés : JPG, PNG et WEBP."
            );
        }
    }

    /**
     * Récupère l'extension du fichier original.
     */
    private String getExtension(String filename) {

        int lastDot = filename.lastIndexOf('.');

        if (lastDot == -1) {
            throw new IllegalArgumentException(
                    "Le fichier image doit avoir une extension."
            );
        }

        return filename.substring(lastDot).toLowerCase();
    }
}

