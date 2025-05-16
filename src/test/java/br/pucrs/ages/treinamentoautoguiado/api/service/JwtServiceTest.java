package br.pucrs.ages.treinamentoautoguiado.api.service;

import br.pucrs.ages.treinamentoautoguiado.api.util.ApiRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private br.pucrs.ages.treinamentoautoguiado.api.entity.User userDetails;
    private String token;

    @BeforeEach
    void setUp() {
        String secretKey = "secretkeysecretkeysecretkeysecretkeysecretkeysecretkeysecretkeysecretkey";
        long jwtExpirationInMs = 1000 * 60 * 5; // 5 min
        long refreshTokenExpirationInMs = 1000 * 60 * 60 * 24; // 24 h

        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationInMs", jwtExpirationInMs);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpirationInMs", refreshTokenExpirationInMs);

        userDetails = new br.pucrs.ages.treinamentoautoguiado.api.entity.User("user", "password","5551981070960","test");
        token = jwtService.generateToken(userDetails);
    }

    @Test
    void testExtractUsername_shouldReturnUsername_whenUserDetailsIsValid() {
        String username = jwtService.extractUsername(token);
        assertEquals("user", username);
    }

    @Test
    void testGenerateToken_shouldReturnAccessToken_whenUserDetailsArePresent() {
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGenerateRefreshToken_shouldReturnRefreshToken_whenUserDetailsArePresent() {
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
    }

    @Test
    void testIsTokenValid_shouldReturnTrue_whenTokenIsValid() {
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testIsTokenValid_shouldReturnFalse_whenTokenIsInvalid() {
        String invalidToken = token.substring(0, token.length() - 1);
        assertFalse(jwtService.isTokenValid(invalidToken, userDetails));
    }

    @Test
    void testIsTokenValid_shouldReturnFalse_whenTokenUserIsDifferent() {
        userDetails = new br.pucrs.ages.treinamentoautoguiado.api.entity.User("different_user", "password","5551981070960","test");
        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testIsTokenValid_shouldReturnFalse_whenTokenIsExpired() {
        ReflectionTestUtils.setField(jwtService, "jwtExpirationInMs", -10000);

        String expiredToken = jwtService.generateToken(userDetails);

        assertFalse(jwtService.isTokenValid(expiredToken, userDetails));
    }

    @Test
    void testExtractAllClaims_shouldThrowException_whenTokenIsInvalid() {
        String token = "xxxx";

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> jwtService.extractAllClaims(token));

        assertEquals("Token inválido", exception.getMessage());
    }
}
