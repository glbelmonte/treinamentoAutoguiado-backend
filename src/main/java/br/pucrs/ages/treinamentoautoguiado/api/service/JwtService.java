package br.pucrs.ages.treinamentoautoguiado.api.service;

import br.pucrs.ages.treinamentoautoguiado.api.entity.User;
import br.pucrs.ages.treinamentoautoguiado.api.util.ApiRequestException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Slf4j
@Service
@Getter
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpirationInMs;

    @Value("${security.jwt.refresh-token-expiration-time}")
    private long refreshTokenExpirationInMs;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateRefreshToken(User userDetails) {
        return generateRefreshToken(new HashMap<>(), userDetails);
    }

    private String generateToken(HashMap<String,Object> extraClaims, User userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpirationInMs);
    }

    private String generateRefreshToken(HashMap<String, Object> extraClaims, User userDetails) {
        return buildToken(extraClaims, userDetails, refreshTokenExpirationInMs);
    }

    private String buildToken(HashMap<String, Object> extraClaims, User userDetails, long expiration) {

        return Jwts.builder()
                .setClaims(extraClaims)
                .claim("roles", userDetails.getAuthorities())
                .claim("id", userDetails.getId())
                .claim("cpf", userDetails.getCpf())
                .claim("nome", userDetails.getNome())
                .claim("email", userDetails.getEmail())
                .claim("is_first_access", userDetails.getIsFirstAccess())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration ))
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            if (!username.equals(userDetails.getUsername())) return false;
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (Exception e) {
            throw new ApiRequestException("Token inválido");
        }
    }

    public Key getSignInKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
