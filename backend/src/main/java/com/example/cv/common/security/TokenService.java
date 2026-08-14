package com.example.cv.common.security;

import com.example.cv.common.model.RoleRef;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

@Service
public class TokenService {
    private final String accessSecret;
    private final String refreshSecret;
    private final long accessExpireMs;
    private final long refreshExpireMs;

    public TokenService(
            @Value("${app.jwt.access-secret}") String accessSecret,
            @Value("${app.jwt.refresh-secret}") String refreshSecret,
            @Value("${app.jwt.access-expire-ms}") long accessExpireMs,
            @Value("${app.jwt.refresh-expire-ms}") long refreshExpireMs) {
        this.accessSecret = accessSecret;
        this.refreshSecret = refreshSecret;
        this.accessExpireMs = accessExpireMs;
        this.refreshExpireMs = refreshExpireMs;
    }

    public String accessToken(CurrentUser user) {
        return encode(accessSecret, user, accessExpireMs);
    }

    public String refreshToken(CurrentUser user) {
        return encode(refreshSecret, user, refreshExpireMs);
    }

    public Jwt decodeAccess(String token) {
        return decoder(accessSecret).decode(token);
    }

    public Jwt decodeRefresh(String token) {
        return decoder(refreshSecret).decode(token);
    }

    private String encode(String secret, CurrentUser user, long expiresInMs) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("from server")
                .subject(user.id())
                .issuedAt(now)
                .expiresAt(now.plusMillis(expiresInMs))
                .claim("_id", user.id())
                .claim("name", user.name())
                .claim("email", user.email())
                .claim("role", Map.of("_id", user.role().getId(), "name", user.role().getName()))
                .build();
        return encoder(secret).encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private JwtEncoder encoder(String secret) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(key(secret).getEncoded()));
    }

    private JwtDecoder decoder(String secret) {
        return NimbusJwtDecoder.withSecretKey(key(secret)).macAlgorithm(MacAlgorithm.HS256).build();
    }

    private SecretKey key(String secret) {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    public static RoleRef roleFromClaim(Jwt jwt) {
        Object role = jwt.getClaims().get("role");
        if (role instanceof Map<?, ?> roleMap) {
            return new RoleRef(String.valueOf(roleMap.get("_id")), String.valueOf(roleMap.get("name")));
        }
        return new RoleRef(null, String.valueOf(role));
    }
}
