package br.pucrs.ages.treinamentoautoguiado.api.controller;

import br.pucrs.ages.treinamentoautoguiado.api.entity.ModuleItem;
import br.pucrs.ages.treinamentoautoguiado.api.entity.User;
import br.pucrs.ages.treinamentoautoguiado.api.model.ModuleItemType;
import br.pucrs.ages.treinamentoautoguiado.api.responses.*;
import br.pucrs.ages.treinamentoautoguiado.api.service.ModuleService;
import br.pucrs.ages.treinamentoautoguiado.api.util.ApiExceptionHandler;
import br.pucrs.ages.treinamentoautoguiado.api.util.ApiRequestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class ModuleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ModuleService moduleService;

    @InjectMocks
    private ModuleController moduleController;


    @BeforeEach
    void setUp() {
        User user = new User("test@email.com", "password", "5551981070960", "test");

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc = MockMvcBuilders
                .standaloneSetup(moduleController)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void testGetAllModulesReturns200() throws Exception {
        ModuleResponseData module1 = ModuleResponseData.builder().id(1L).name("Módulo 1").order(1).build();
        ModuleResponseData module2 = ModuleResponseData.builder().id(2L).name("Módulo 2").order(2).build();

        ModuleResponse response = ModuleResponse.builder()
                .progress("0%")
                .data(List.of(module1, module2))
                .build();

        when(moduleService.getAllModules(any())).thenReturn(response);

        mockMvc.perform(get("/modules")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progress").value("0%"))
                .andExpect(jsonPath("$.data.size()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("Módulo 1"))
                .andExpect(jsonPath("$.data[1].order").value(2));
    }

    @Test
    void testGetModulePhases_shouldReturnModulePhases_whenModuleExists() throws Exception {
        List<ModulePhaseData> phases = List.of(
                new ModulePhaseData(1, "Introdução", "0%"),
                new ModulePhaseData(2, "Atividade", "0%")
        );
        ModulePhaseResponse response = new ModulePhaseResponse("0%", "Módulo Teste", phases);
        when(moduleService.getModulePhases(any(), any())).thenReturn(response);

        mockMvc.perform(get("/modules/1/phases")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.module_name").value("Módulo Teste"))
                .andExpect(jsonPath("$.progress").value("0%"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].phase").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Introdução"));
    }

    @Test
    void testGetModulePhases_shouldReturnNotFound_whenModuleDoesNotExist() throws Exception {
        when(moduleService.getModulePhases(any(), any()))
                .thenThrow(new ApiRequestException("Módulo não encontrado"));

        mockMvc.perform(get("/modules/99/phases")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Módulo não encontrado"));
    }

    @Test
    void testGetPhaseModuleItems_shouldReturnPhaseModuleItems_whenModuleExists() throws Exception {
        List<ModuleItemsResponse> response = List.of(
                new ModuleItemsResponse(1L, "test1", 1, ModuleItemType.TEXT, 1, "resposta"));


        when(moduleService.fetchPhaseModuleItems(any(), any(), any())).thenReturn(response);

        mockMvc.perform(get("/modules/1/phase/1/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].text").value("test1"))
                .andExpect(jsonPath("$.[0].modulePhase").value(1))
                .andExpect(jsonPath("$.[0].type").value("TEXT"))
                .andExpect(jsonPath("$.[0].moduleItemOrder").value(1))
                .andExpect(jsonPath("$.[0].value").value("resposta"));

    }
}
