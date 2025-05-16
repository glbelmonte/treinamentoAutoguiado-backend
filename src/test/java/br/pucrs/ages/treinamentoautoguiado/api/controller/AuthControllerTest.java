package br.pucrs.ages.treinamentoautoguiado.api.controller;

import br.pucrs.ages.treinamentoautoguiado.api.dto.LoginUserDTO;
import br.pucrs.ages.treinamentoautoguiado.api.dto.RegisterUserDTO;
import br.pucrs.ages.treinamentoautoguiado.api.entity.User;
import br.pucrs.ages.treinamentoautoguiado.api.responses.AuthResponse;
import br.pucrs.ages.treinamentoautoguiado.api.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testRegister_ShouldReturnAuthResponse_WhenValidRequest() throws Exception {
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        Date expiresIn = new Date(System.currentTimeMillis() + 3600 * 1000);
        AuthResponse authResponse = new AuthResponse(
                "success",
                "Usuário cadastrado com sucesso",
                new User("test@email.com", "encodedPassword","33355331007","test"),
                accessToken,
                refreshToken,
                expiresIn
        );
        String json = "{\"password\":\"password\",\"matchingPassword\":\"password\",\"email\":\"test@email.com\",\"cpf\":\"33355331007\",\"nome\":\"test\"}";

        when(authService.signup(any(RegisterUserDTO.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.user.email").value("test@email.com"))
                .andExpect(jsonPath("$.data.user.cpf").value("33355331007"))
                .andExpect(jsonPath("$.data.user.nome").value("test"))
                .andExpect(jsonPath("$.data.user.is_first_access").value("true"))
                .andExpect(jsonPath("$.data.token.access_token").value(accessToken))
                .andExpect(jsonPath("$.data.token.refresh_token").value(refreshToken));

        verify(authService, times(1)).signup(any(RegisterUserDTO.class));
    }

    @Test
    void testAuthenticate_ShouldReturnAuthResponse_WhenValidRequest() throws Exception {
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        Date expiresIn = new Date(System.currentTimeMillis() + 3600 * 1000);
        AuthResponse authResponse = new AuthResponse(
                "success",
                "Usuário autenticado com sucesso",
                new User("test@email.com", "password","33355331007","teste"),
                accessToken,
                refreshToken,
                expiresIn
        );
        String json = "{\"password\":\"password\",\"email\":\"test@email.com\"}";

        when(authService.signin(any(LoginUserDTO.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.user.email").value("test@email.com"))
                .andExpect(jsonPath("$.data.user.cpf").value("33355331007"))
                .andExpect(jsonPath("$.data.user.nome").value("teste"))
                .andExpect(jsonPath("$.data.user.email").value("test@email.com"))
                .andExpect(jsonPath("$.data.user.is_first_access").value("true"))
                .andExpect(jsonPath("$.data.token.access_token").value(accessToken))
                .andExpect(jsonPath("$.data.token.refresh_token").value(refreshToken));

        verify(authService, times(1)).signin(any(LoginUserDTO.class));
    }

    @Test
    void testRefresh_ShouldReturnAuthResponse_WhenValidRequest() throws Exception {
        String accessToken = "newAccessToken";
        String refreshToken = "newRefreshToken";
        Date expiresIn = new Date(System.currentTimeMillis() + 3600 * 1000);
        AuthResponse authResponse = new AuthResponse(
                "success",
                "Refresh token gerado com sucesso",
                new User("test@email.com", "encodedPassword","33355331007","test"),
                accessToken,
                refreshToken,
                expiresIn
        );
        String refreshTokenRequest = "validRefreshToken";

        when(authService.refreshToken(refreshTokenRequest)).thenReturn(authResponse);

        mockMvc.perform(post("/auth/refresh")
                        .param("refresh_token", refreshTokenRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.token.access_token").value(accessToken))
                .andExpect(jsonPath("$.data.token.refresh_token").value(refreshToken));

        verify(authService, times(1)).refreshToken(refreshTokenRequest);
    }

    @Test
    void testValidateToken_ShouldReturnTrue_WhenTokenIsValid() throws Exception {
        String validToken = "validAccessToken123";

        when(authService.validateToken(validToken)).thenReturn(true);

        mockMvc.perform(get("/auth/validateToken")
                        .param("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(authService, times(1)).validateToken(validToken);
    }

    @Test
    void testValidateToken_ShouldReturnFalse_WhenTokenIsInvalid() throws Exception {
        String invalidToken = "invalidAccessToken123";

        when(authService.validateToken(invalidToken)).thenReturn(false);

        mockMvc.perform(get("/auth/validateToken")
                        .param("token", invalidToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        verify(authService, times(1)).validateToken(invalidToken);
    }

}
