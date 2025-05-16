package br.pucrs.ages.treinamentoautoguiado.api.controller;

import br.pucrs.ages.treinamentoautoguiado.api.dto.LoginUserDTO;
import br.pucrs.ages.treinamentoautoguiado.api.dto.RegisterUserDTO;
import br.pucrs.ages.treinamentoautoguiado.api.responses.AuthResponse;
import br.pucrs.ages.treinamentoautoguiado.api.service.AuthService;
import br.pucrs.ages.treinamentoautoguiado.api.util.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Tag(name = "Auth", description = "Endpoints relacionados à autenticação e registro de usuários")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registrar um novo usuário", description = "Registra um novo usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário registrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos para o registro",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody @Validated RegisterUserDTO registerUserDto) {
        AuthResponse response = authService.signup(registerUserDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Autenticar usuário", description = "Autentica um usuário com base nas credenciais fornecidas.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário autenticado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Credenciais inválidas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    })
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody @Validated LoginUserDTO loginUserDto){
        AuthResponse response = authService.signin(loginUserDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar token de acesso", description = "Permite a atualização do token de acesso usando um refresh token válido.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Refresh token inválido ou expirado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiException.class))),
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestParam("refresh_token") String refreshToken) {
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Validar token", description = "Valida se o token de acesso fornecido é válido.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Resultado da validação.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
    })
    @GetMapping("/validateToken")
    public ResponseEntity<Boolean> validateToken(@RequestParam("token") String token) {
        boolean response = authService.validateToken(token);
        return ResponseEntity.ok(response);
    }
}
