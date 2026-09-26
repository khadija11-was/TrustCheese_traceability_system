package ma.trustCheese.TrustCheesebackend.controller;



import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.AuthResponse;
import ma.trustCheese.TrustCheesebackend.dto.LoginRequest;
import ma.trustCheese.TrustCheesebackend.service.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final AuthService authService;


    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {

        AuthResponse authResponse =
                authService.login(request, response);

        return ResponseEntity.ok(authResponse);
    }


    // =========================================================
    // REFRESH TOKEN
    // =========================================================

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(
                    name = REFRESH_TOKEN_COOKIE,
                    required = false
            )
            String refreshToken,
            HttpServletResponse response
    ) {

                if (refreshToken == null || refreshToken.isBlank()) {
                        return ResponseEntity
                                        .status(HttpStatus.UNAUTHORIZED)
                                        .build();
                }

        AuthResponse authResponse =
                authService.refreshToken(
                        refreshToken,
                        response
                );

        return ResponseEntity.ok(authResponse);
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletResponse response
    ) {

        authService.logout(response);

        return ResponseEntity.noContent().build();
    }
}
