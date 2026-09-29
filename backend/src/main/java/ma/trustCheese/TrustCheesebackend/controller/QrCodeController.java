package ma.trustCheese.TrustCheesebackend.controller;



import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.service.QrCodeService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/lots")
@RequiredArgsConstructor
public class QrCodeController {

    private final QrCodeService qrCodeService;

    @GetMapping(
            value = "/{numeroLot}/qrcode",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public ResponseEntity<byte[]> generateQrCode(
            @PathVariable String numeroLot
    ) {

        byte[] qrCode = qrCodeService.generateQrCode(numeroLot);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(
                        CacheControl.maxAge(1, TimeUnit.HOURS)
                )
                .body(qrCode);
    }
}