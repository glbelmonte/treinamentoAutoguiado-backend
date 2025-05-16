package br.pucrs.ages.treinamentoautoguiado.api.service;

import br.pucrs.ages.treinamentoautoguiado.api.dto.LoginUserDTO;
import br.pucrs.ages.treinamentoautoguiado.api.dto.RegisterUserDTO;
import br.pucrs.ages.treinamentoautoguiado.api.entity.User;
import br.pucrs.ages.treinamentoautoguiado.api.repository.UserRepository;
import br.pucrs.ages.treinamentoautoguiado.api.responses.AuthResponse;
import br.pucrs.ages.treinamentoautoguiado.api.util.ApiRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User user;
    private LoginUserDTO loginUserDTO;
    private RegisterUserDTO registerUserDTO;
    private final String accessToken = "mockAccessToken";
    private final String refreshToken = "mockRefreshToken";
    private final Date expiresIn = new Date(System.currentTimeMillis() + 1000 * 60 * 5); // 5 min

    @BeforeEach
    void setUp() {
        user = new User("test@example.com", "enconded_pass","5551981070960","test");
        loginUserDTO = new LoginUserDTO("test@example.com", "raw_pass");
        registerUserDTO = new RegisterUserDTO("raw_password", "raw_password", "test@example.com","test","5551981070960");
    }

    @Test
    void testSignin_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        when(userRepository.findByEmailAndIsDeletedFalse(loginUserDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginUserDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(user)).thenReturn(refreshToken);
        when(jwtService.extractExpiration(accessToken)).thenReturn(expiresIn);

        AuthResponse response = authService.signin(loginUserDTO);

        assertNotNull(response);
        assertEquals("success", response.getStatus());

        verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(loginUserDTO.getEmail());
        verify(passwordEncoder, times(1)).matches(loginUserDTO.getPassword(), user.getPassword());
        verify(jwtService, times(1)).generateToken(user);
        verify(jwtService, times(1)).generateRefreshToken(user);
        verify(jwtService, times(1)).extractExpiration(accessToken);
    }

    @Test
    void testSignin_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByEmailAndIsDeletedFalse(loginUserDTO.getEmail())).thenReturn(Optional.empty());

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> authService.signin(loginUserDTO));

        assertEquals("E-mail ou senha incorretos", exception.getMessage());
        verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(loginUserDTO.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void testSignin_ShouldThrowException_WhenPasswordIsIncorrect() {
        when(userRepository.findByEmailAndIsDeletedFalse(loginUserDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginUserDTO.getPassword(), user.getPassword())).thenReturn(false);

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> authService.signin(loginUserDTO));

        assertEquals("E-mail ou senha incorretos", exception.getMessage());
        verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(loginUserDTO.getEmail());
        verify(passwordEncoder, times(1)).matches(loginUserDTO.getPassword(), user.getPassword());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void testSignup_ShouldReturnAuthResponse_WhenUserIsNotRegisteredAndCredentialsAreValid() {
        when(userRepository.findByEmailAndIsDeletedFalse(registerUserDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerUserDTO.getPassword())).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(user)).thenReturn(refreshToken);
        when(jwtService.extractExpiration(accessToken)).thenReturn(expiresIn);

        AuthResponse response = authService.signup(registerUserDTO);

        assertNotNull(response);
        assertEquals("success", response.getStatus());

        verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(registerUserDTO.getEmail());
        verify(passwordEncoder, times(1)).encode(registerUserDTO.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(user);
        verify(jwtService, times(1)).generateRefreshToken(user);
        verify(jwtService, times(1)).extractExpiration(accessToken);
    }

    @Test
    void testSignup_ShouldThrowException_WhenUserAlreadyExists() {
        when(userRepository.findByEmailAndIsDeletedFalse(registerUserDTO.getEmail())).thenReturn(Optional.of(user));

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> authService.signup(registerUserDTO));

        assertEquals("Já existe um usuário cadastrado com este e-mail", exception.getMessage());
        verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(registerUserDTO.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void testRefreshToken_ShouldReturnAuthResponse_WhenTokenIsValid() {
        when(jwtService.extractUsername(refreshToken)).thenReturn(user.getEmail());
        when(userRepository.findByEmailAndIsDeletedFalse(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(refreshToken, user)).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("newAccessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("newRefreshToken");
        when(jwtService.extractExpiration("newAccessToken")).thenReturn(expiresIn);

        AuthResponse response = authService.refreshToken(refreshToken);

        assertNotNull(response);
        assertEquals("success", response.getStatus());

        verify(jwtService, times(1)).extractUsername(refreshToken);
        verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(user.getEmail());
        verify(jwtService, times(1)).isTokenValid(refreshToken, user);
        verify(jwtService, times(1)).generateToken(user);
        verify(jwtService, times(1)).generateRefreshToken(user);
        verify(jwtService, times(1)).extractExpiration("newAccessToken");
    }

    @Test
    void testRefreshToken_ShouldThrowException_WhenUserNotFound() {
        when(jwtService.extractUsername(refreshToken)).thenReturn(user.getEmail());
        when(userRepository.findByEmailAndIsDeletedFalse(user.getEmail())).thenReturn(Optional.empty());

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> authService.refreshToken(refreshToken));

        assertEquals("Refresh token inválido", exception.getMessage());
        verify(jwtService, times(1)).extractUsername(refreshToken);
        verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(user.getEmail());
        verify(jwtService, never()).isTokenValid(anyString(), any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void testRefreshToken_ShouldThrowException_WhenTokenIsInvalid() {
        when(jwtService.extractUsername(refreshToken)).thenReturn(user.getEmail());
        when(userRepository.findByEmailAndIsDeletedFalse(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(refreshToken, user)).thenReturn(false);

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> authService.refreshToken(refreshToken));

        assertEquals("Refresh token inválido", exception.getMessage());
        verify(jwtService, times(1)).extractUsername(refreshToken);
        verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(user.getEmail());
        verify(jwtService, times(1)).isTokenValid(refreshToken, user);
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void testValidateToken_ShouldReturnTrue_WhenTokenIsValid() {
        when(jwtService.extractUsername(accessToken)).thenReturn(user.getEmail());
        when(jwtService.isTokenValid(accessToken, user)).thenReturn(true);
        when(userRepository.findByEmailAndIsDeletedFalse(user.getEmail())).thenReturn(Optional.of(user));

        boolean response = authService.validateToken(accessToken);

        assertTrue(response);

        verify(jwtService, times(1)).extractUsername(accessToken);
        verify(jwtService, times(1)).isTokenValid(accessToken, user);
        verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(user.getEmail());
    }

    @Test
    void testValidateToken_ShouldReturnFalse_WhenTokenIsInvalid() {
        when(jwtService.extractUsername(accessToken)).thenReturn(user.getEmail());
        when(jwtService.isTokenValid(accessToken, user)).thenReturn(false);
        when(userRepository.findByEmailAndIsDeletedFalse(user.getEmail())).thenReturn(Optional.of(user));
        boolean response = authService.validateToken(accessToken);
        assertFalse(response);

        verify(jwtService, times(1)).extractUsername(accessToken);
        verify(jwtService, times(1)).isTokenValid(accessToken, user);
        verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(user.getEmail());
    }

    @Test
    void testValidateToken_ShouldReturnFalse_WhenUserNotFound() {
        when(jwtService.extractUsername(accessToken)).thenReturn(user.getEmail());
        when(userRepository.findByEmailAndIsDeletedFalse(user.getEmail())).thenReturn(Optional.empty());
        boolean response = authService.validateToken(accessToken);
        assertFalse(response);

        verify(jwtService, times(1)).extractUsername(accessToken);
        verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(user.getEmail());
    }

    @Test
    void testValidateToken_ShouldThrowException_WhenTokenIsInvalid() {
        when(jwtService.extractUsername(accessToken)).thenThrow(new ApiRequestException("Token invalido"));

        boolean response = authService.validateToken(accessToken);
        assertFalse(response);

        verify(jwtService, times(1)).extractUsername(accessToken);
    }
}
