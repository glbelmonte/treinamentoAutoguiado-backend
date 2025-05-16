package br.pucrs.ages.treinamentoautoguiado.api.controller;

import br.pucrs.ages.treinamentoautoguiado.api.entity.User;
import br.pucrs.ages.treinamentoautoguiado.api.responses.AnswersResponse;
import br.pucrs.ages.treinamentoautoguiado.api.service.AnswersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@Log4j2
@RestController
@RequestMapping("/answers")
@RequiredArgsConstructor
@Tag(name = "Respostas", description = "Endpoints relacionados às respostas dos usuários")
public class AnswersController {

    private final AnswersService answerService;

    @Operation(
            summary = "Obter respostas de um usuário por módulo",
            description = "Retorna as respostas submetidas por um usuário em um módulo específico"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Respostas retornadas com sucesso",
                    content = @Content(schema = @Schema(implementation = AnswersResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autorizado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "404",
                    description = "Módulo ou usuário não encontrado",
                    content = @Content(mediaType = "application/json"))
    })

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<AnswersResponse> getAnswersByModule(@PathVariable Long moduleId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        AnswersResponse response = answerService.getAnswersByUserAndModule(user.getId(), moduleId);


        return ResponseEntity.ok(response);
    }
}
