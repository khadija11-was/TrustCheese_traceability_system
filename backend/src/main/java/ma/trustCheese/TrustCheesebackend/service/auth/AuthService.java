package ma.trustCheese.TrustCheesebackend.service.auth;




import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.dto.AuthResponse;
import ma.trustCheese.TrustCheesebackend.dto.LoginRequest;
import ma.trustCheese.TrustCheesebackend.entity.Utilisateur;
import ma.trustCheese.TrustCheesebackend.repository.UtilisateurRepository;
import ma.trustCheese.TrustCheesebackend.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String ACCESS_TOKEN_COOKIE = "access_token";
    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final UtilisateurRepository utilisateurRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Value("${jwt.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpiration;


    // =========================================================
    // LOGIN
    // =========================================================

    public AuthResponse login(
            LoginRequest request,
            HttpServletResponse response
    ) {

        // 1. Authentification email + mot de passe
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getMotDePasse()
                        )
                );

        // 2. Récupérer UserDetails depuis l'authentification
        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        // 3. Récupérer l'utilisateur métier depuis la DB
        Utilisateur utilisateur =
                utilisateurRepository.findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Utilisateur non trouvé"
                                )
                        );

        // 4. Générer les deux tokens
        String accessToken =
                jwtService.generateAccessToken(userDetails);

        String refreshToken =
                jwtService.generateRefreshToken(userDetails);

        // 5. Mettre les tokens dans les cookies
        addAccessTokenCookie(
                response,
                accessToken
        );

        addRefreshTokenCookie(
                response,
                refreshToken
        );

        // 6. Retourner uniquement les informations utilisateur
        return AuthResponse.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole())
                .build();
    }


    // =========================================================
    // REFRESH TOKEN
    // =========================================================

    public AuthResponse refreshToken(
            String refreshToken,
            HttpServletResponse response
    ) {

        // 1. Vérifier que le refresh token existe
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RuntimeException(
                    "Refresh token manquant"
            );
        }

        // 2. Extraire l'email du token
        String email;

        try {
            email = jwtService.extractEmail(refreshToken);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Refresh token invalide"
            );
        }

        // 3. Charger UserDetails
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(email);

        // 4. Valider le refresh token
        if (!jwtService.validateRefreshToken(
                refreshToken,
                userDetails
        )) {
            throw new RuntimeException(
                    "Refresh token invalide ou expiré"
            );
        }

        // 5. Récupérer l'utilisateur
        Utilisateur utilisateur =
                utilisateurRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Utilisateur non trouvé"
                                )
                        );

        // 6. Générer un nouveau access token
        String newAccessToken =
                jwtService.generateAccessToken(userDetails);

        // 7. Remplacer l'ancien access token
        addAccessTokenCookie(
                response,
                newAccessToken
        );

        // 8. Retourner les informations utilisateur
        return AuthResponse.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole())
                .build();
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    public void logout(
            HttpServletResponse response
    ) {

        deleteCookie(
                response,
                ACCESS_TOKEN_COOKIE
        );

        deleteCookie(
                response,
                REFRESH_TOKEN_COOKIE
        );
    }


    // =========================================================
    // ACCESS TOKEN COOKIE
    // =========================================================

    private void addAccessTokenCookie(
            HttpServletResponse response,
            String accessToken
    ) {

        ResponseCookie cookie =
                ResponseCookie.from(
                                ACCESS_TOKEN_COOKIE,
                                accessToken
                        )
                        .httpOnly(true)
                        .secure(false) // true en production HTTPS
                        .sameSite("Lax")
                        .path("/")
                        .maxAge(
                                Duration.ofMillis(
                                        accessTokenExpiration
                                )
                        )
                        .build();

        response.addHeader(
                "Set-Cookie",
                cookie.toString()
        );
    }


    // =========================================================
    // REFRESH TOKEN COOKIE
    // =========================================================

    private void addRefreshTokenCookie(
            HttpServletResponse response,
            String refreshToken
    ) {

        ResponseCookie cookie =
                ResponseCookie.from(
                                REFRESH_TOKEN_COOKIE,
                                refreshToken
                        )
                        .httpOnly(true)
                        .secure(false) // true en production HTTPS
                        .sameSite("Lax")
                        .path("/")
                        .maxAge(
                                Duration.ofMillis(
                                        refreshTokenExpiration
                                )
                        )
                        .build();

        response.addHeader(
                "Set-Cookie",
                cookie.toString()
        );
    }


    // =========================================================
    // DELETE COOKIE
    // =========================================================

    private void deleteCookie(
            HttpServletResponse response,
            String cookieName
    ) {

        ResponseCookie cookie =
                ResponseCookie.from(
                                cookieName,
                                ""
                        )
                        .httpOnly(true)
                        .secure(false)
                        .sameSite("Lax")
                        .path("/")
                        .maxAge(Duration.ZERO)
                        .build();

        response.addHeader(
                "Set-Cookie",
                cookie.toString()
        );
    }
}