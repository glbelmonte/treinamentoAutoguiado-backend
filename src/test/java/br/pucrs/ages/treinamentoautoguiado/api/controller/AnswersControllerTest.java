package br.pucrs.ages.treinamentoautoguiado.api.controller;

import br.pucrs.ages.treinamentoautoguiado.api.entity.User;
import br.pucrs.ages.treinamentoautoguiado.api.responses.AnswersResponse;
import br.pucrs.ages.treinamentoautoguiado.api.service.AnswersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class AnswersControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AnswersService answersService;

    @InjectMocks
    private AnswersController answersController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(answersController).build();
        User user = User.builder().id(1L).build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, List.of())
        );
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetAnswersByModule_ShouldReturnAnswersList_WhenModuleDoesExist() throws Exception {
        AnswersResponse mockResponse = AnswersResponse.builder()
                .moduleName("Módulo Teste")
                .answers(List.of("Resposta 1"))
                .build();

        when(answersService.getAnswersByUserAndModule(1L, 10L))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/answers/module/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("module_name").value("Módulo Teste"))
                .andExpect(jsonPath("answers[0]").value("Resposta 1"));
    }

}
