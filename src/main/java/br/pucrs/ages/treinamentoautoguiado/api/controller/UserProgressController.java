package br.pucrs.ages.treinamentoautoguiado.api.controller;

import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema; 
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;

import br.pucrs.ages.treinamentoautoguiado.api.dto.UserProgressDTO;
import br.pucrs.ages.treinamentoautoguiado.api.service.UserProgressService;

@Log4j2
@RestController
@RequestMapping("/user-progress")
@AllArgsConstructor
@Tag(name = "UserProgress", description = "Endpoints relacionados ao progresso do usuário")
public class UserProgressController {

    private final UserProgressService userProgressService;

    @Operation(
        summary = "Salvar progresso do usuário",
        description = "Salva o progresso de um usuário em um item do módulo."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Progresso salvo com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos para salvar progresso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse( 
            responseCode = "500",
            description = "Erro interno do servidor",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("")
    public ResponseEntity<?> saveProgress(@RequestBody @Validated UserProgressDTO userProgressDTO) { 
        userProgressService.saveUserProgress(userProgressDTO); 
        return ResponseEntity.noContent().build(); 
    }
}
