package com.psychology.product.service.jwt;

import com.psychology.product.repository.TokenRepository;
import com.psychology.product.repository.model.TokenDAO;
import com.psychology.product.service.JwtUtils;
import com.psychology.product.service.impl.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
@Slf4j
public class JwtUtilsImpl implements JwtUtils {

    @Value("${jwt.secret.access.expirationMs}")
    private long jwtAccessExpirationMs;
    @Value("${jwt.secret.refresh.expirationMs}")
    private long jwtRefreshExpirationMs;

    private final Key jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;
    private final TokenRepository tokenRepository;

    public JwtUtilsImpl(
            @Value(("${jwt.secret.access}")) String jwtAccessSecret,
            @Value(("${jwt.secret.refresh}")) String jwtRefreshSecret,
            TokenRepository tokenRepository) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
        this.tokenRepository = tokenRepository;
    }

    public String generateJwtToken(@NonNull Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        final Date expiration = new Date(new Date().getTime() + jwtAccessExpirationMs);

        return Jwts.builder()
                .claim("id", userPrincipal.getUser().getId())
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(jwtAccessSecret)
                .compact();
    }

    public String generateRefreshToken(@NonNull Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        final Date expiration = new Date(new Date().getTime() + jwtRefreshExpirationMs);
        return Jwts.builder()
                .subject((userPrincipal.getUsername()))
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) jwtAccessSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtAccessToken(@NonNull String token) {
        return validateJwtToken(token, jwtAccessSecret);
    }

    public boolean validateJwtRefreshToken(@NonNull String token) {
        TokenDAO tokenDAO = tokenRepository.findByToken(token);
        return validateJwtToken(token, jwtRefreshSecret) && (!tokenDAO.isExpired() && !tokenDAO.isRevoked());
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    public boolean validateJwtToken(@NonNull String token, @NonNull Key secret) {
        try {
            Jwts.parser().verifyWith((SecretKey) secret).build().parse(token);
            return true;

        } catch (ExpiredJwtException e) {
            TokenDAO tokenDAO = tokenRepository.findByToken(token);
            Optional.ofNullable(tokenDAO).ifPresent(tokenDAOPresent -> {
                tokenDAOPresent.setExpired(true);
                tokenRepository.save(tokenDAO);
            });
            log.error("JWT token is expired");
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty");
        } catch (SignatureException e) {
            log.error("JWT signature does not match locally computed signature");
        }

        return false;
    }

    private Claims getClaims(@NonNull String token, @NonNull Key jwtRefreshSecret) {
        return Jwts.parser()
                .verifyWith((SecretKey) jwtRefreshSecret)
                .decryptWith((SecretKey) jwtRefreshSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
