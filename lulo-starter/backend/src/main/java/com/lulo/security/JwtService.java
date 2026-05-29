package com.lulo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expiryMs;

    public JwtService(
            @Value("${lulo.jwt.secret:lulo-secret-key-change-in-production-please-make-it-long-enough-256bits}") String secret,
            @Value("${lulo.jwt.expiry-ms:3600000}") long expiryMs) {
        // HS256 requiere clave de al menos 256 bits (32 bytes).
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("lulo.jwt.secret debe tener al menos 32 caracteres");
        }
        this.signingKey = Keys.hmacShaKeyFor(bytes);
        this.expiryMs = expiryMs;
    }

    /**
     * Genera JWT con la identidad mínima: sub, email, empresaId y tipoUsuario.
     * Los permisos NO viajan en el token; se resuelven en el filtro a partir de
     * tipoUsuario + datos de BD. Beneficios: token más pequeño y permisos
     * siempre frescos (un cambio de rol surte efecto sin esperar refresh).
     */
    public String generate(UUID usuarioId,
                           String email,
                           UUID empresaId,
                           String tipoUsuario) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiryMs);
        return Jwts.builder()
                .subject(usuarioId.toString())
                .claim("email", email)
                .claim("empresaId", empresaId == null ? null : empresaId.toString())
                .claim("tipoUsuario", tipoUsuario)
                .issuedAt(now)
                .expiration(exp)
                .signWith(signingKey)
                .compact();
    }

    /** Sobrecarga para compatibilidad: ignora la lista de permisos. */
    public String generate(UUID usuarioId, String email, UUID empresaId,
                           String tipoUsuario, List<String> ignoredPermisos) {
        return generate(usuarioId, email, empresaId, tipoUsuario);
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpiryMs() {
        return expiryMs;
    }

    public Map<String, Object> describe(Claims claims) {
        return Map.of(
                "usuarioId", claims.getSubject(),
                "email", claims.get("email", String.class),
                "empresaId", claims.get("empresaId", String.class),
                "tipoUsuario", claims.get("tipoUsuario", String.class)
        );
    }
}
