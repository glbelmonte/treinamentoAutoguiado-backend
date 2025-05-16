package br.pucrs.ages.treinamentoautoguiado.api.controller;

import br.pucrs.ages.treinamentoautoguiado.api.dto.UpdateUserDTO;
import br.pucrs.ages.treinamentoautoguiado.api.entity.User;
import br.pucrs.ages.treinamentoautoguiado.api.responses.UserResponse;
import br.pucrs.ages.treinamentoautoguiado.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Tag(name = "User", description = "Endpoints relacionados ao gerenciamento de usuários")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Obter informações do usuário autenticado",
            description = "Retorna as informações do usuário que está autenticado no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Informações do usuário autenticado retornadas com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autorizado - Usuário não autenticado")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(new UserResponse(currentUser));
    }

    @Operation(
            summary = "Altera as informações do usuário autenticado",
            description = "Altera as informações do usuário que está autenticado no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Informações do usuário autenticado alteradas com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autorizado - Usuário não autenticado")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody @Validated UpdateUserDTO updateUserDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        User updatedUser = userService.updateUser(id, currentUser, updateUserDto);
        return ResponseEntity.ok(new UserResponse(updatedUser));
    }

    @Operation(
            summary = "Obter todos os usuários",
            description = "Retorna uma lista de todos os usuários no sistema. Apenas utilizável por um administrador."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuários retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado - Requer permissão ADMIN")
    })
    @GetMapping
//    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> response = userService.fetchAllUsers();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Deletar usuário",
            description = "Deleta um usuário do sistema. Apenas utilizável por um administrador."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuário deletado com sucesso"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado - Requer permissão ADMIN"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

}
