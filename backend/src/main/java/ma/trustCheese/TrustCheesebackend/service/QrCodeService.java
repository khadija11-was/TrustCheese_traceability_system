package ma.trustCheese.TrustCheesebackend.service;





import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.entity.LotProduitFini;
import ma.trustCheese.TrustCheesebackend.repository.LotProduitFiniRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrCodeService {

    private final LotProduitFiniRepository lotProduitFiniRepository;

    @Value("${app.public-base-url}")
    private String publicBaseUrl;

    /**
     * Génère le QR Code du lot et retourne directement
     * l'image PNG en mémoire.
     *
     * Aucun fichier n'est sauvegardé sur le disque.
     *
     * @param numeroLot numéro métier du lot
     * @return image PNG du QR Code
     */
    public byte[] generateQrCode(String numeroLot) {

        if (numeroLot == null || numeroLot.isBlank()) {
            throw new IllegalArgumentException(
                    "Le numéro de lot est obligatoire."
            );
        }

        String normalizedNumeroLot = numeroLot.trim();

        // Vérification que le lot existe
        LotProduitFini lot = lotProduitFiniRepository
                .findByNumeroLot(normalizedNumeroLot)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucun lot trouvé pour le numéro : "
                                + normalizedNumeroLot
                ));

        /*
         * Construction de l'URL publique contenue dans le QR Code.
         *
         * Exemple :
         * http://localhost:5173/traceabilite/LOT-20260927-0001
         */
        String publicUrl = buildPublicTraceabilityUrl(
                lot.getNumeroLot()
        );

        return generatePng(publicUrl);
    }

    /**
     * Construit l'URL publique de traçabilité.
     */
    private String buildPublicTraceabilityUrl(String numeroLot) {

        String baseUrl = publicBaseUrl;

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException(
                    "La configuration app.public-base-url est obligatoire."
            );
        }

        // Supprime le "/" final pour éviter un double slash.
        baseUrl = baseUrl.replaceAll("/+$", "");

        return baseUrl
                + "/traceabilite/"
                + numeroLot;
    }

    /**
     * Génère une image PNG du QR Code en mémoire.
     */
    private byte[] generatePng(String content) {

        final int width = 400;
        final int height = 400;

        try {

            Map<EncodeHintType, Object> hints = new HashMap<>();

            // Meilleure tolérance aux petites détériorations
            hints.put(
                    EncodeHintType.ERROR_CORRECTION,
                    com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M
            );

            // Encodage UTF-8
            hints.put(
                    EncodeHintType.CHARACTER_SET,
                    "UTF-8"
            );

            // Marge autour du QR Code
            hints.put(
                    EncodeHintType.MARGIN,
                    1
            );

            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            BitMatrix bitMatrix = qrCodeWriter.encode(
                    content,
                    BarcodeFormat.QR_CODE,
                    width,
                    height,
                    hints
            );

            try (ByteArrayOutputStream outputStream =
                         new ByteArrayOutputStream()) {

                MatrixToImageWriter.writeToStream(
                        bitMatrix,
                        "PNG",
                        outputStream
                );

                return outputStream.toByteArray();
            }

        } catch (WriterException e) {

            throw new IllegalStateException(
                    "Impossible de générer le QR Code.",
                    e
            );

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Impossible de générer l'image du QR Code.",
                    e
            );
        }
    }
}