package br.pucrs.ages.treinamentoautoguiado.api.controller;

import br.pucrs.ages.treinamentoautoguiado.api.entity.User;
import br.pucrs.ages.treinamentoautoguiado.api.responses.ModuleResponse;
import br.pucrs.ages.treinamentoautoguiado.api.responses.ModuleResponseData;
import br.pucrs.ages.treinamentoautoguiado.api.responses.ModulePhaseResponse;
import br.pucrs.ages.treinamentoautoguiado.api.responses.ModuleItemsResponse;
import br.pucrs.ages.treinamentoautoguiado.api.service.ModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/modules")
@RequiredArgsConstructor
@Tag(name = "Módulos", description = "Endpoints relacionados aos módulos do treinamento autoguiado")
public class ModuleController {

    private final ModuleService moduleService;

    @Operation(
            summary = "Obter todos os módulos",
            description = "Retorna uma lista de todos os módulos disponíveis no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de módulos retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ModuleResponseData.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acesso negado")
    })
    @GetMapping
    public ResponseEntity<ModuleResponse> getAllModules() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        ModuleResponse response = moduleService.getAllModules(user.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obter fases de um módulo",
            description = "Retorna todas as fases (etapas) de um módulo específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Fases retornadas com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ModulePhaseResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Módulo não encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autorizado",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{moduleId}/phases")
    public ResponseEntity<ModulePhaseResponse> getModulePhases(@PathVariable Long moduleId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        log.info("Solicitação para listar fases do módulo com ID: {}", moduleId);
        ModulePhaseResponse response = moduleService.getModulePhases(moduleId, user.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obter Module Items de uma fase de um módulo",
            description = "Retorna todos module items de uma fase de um módulo"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Module Items retornados com sucesso",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = ModuleItemsResponse.class)))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Módulo não encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autorizado",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{module_id}/phase/{module_phase}/items")
    public ResponseEntity<List<ModuleItemsResponse>> getPhaseModuleItems(@PathVariable("module_id") Long moduleId, @PathVariable("module_phase") Integer phase) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<ModuleItemsResponse> moduleItems = moduleService.fetchPhaseModuleItems(moduleId, phase, user.getId());
        return ResponseEntity.ok(moduleItems);
    }
}
