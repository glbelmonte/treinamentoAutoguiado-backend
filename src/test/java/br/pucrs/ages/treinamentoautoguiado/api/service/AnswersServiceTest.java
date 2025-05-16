package br.pucrs.ages.treinamentoautoguiado.api.service;

import br.pucrs.ages.treinamentoautoguiado.api.entity.Answer;
import br.pucrs.ages.treinamentoautoguiado.api.entity.Module;
import br.pucrs.ages.treinamentoautoguiado.api.entity.ModuleItem;
import br.pucrs.ages.treinamentoautoguiado.api.repository.AnswersRepository;
import br.pucrs.ages.treinamentoautoguiado.api.repository.ModuleRepository;
import br.pucrs.ages.treinamentoautoguiado.api.responses.AnswersResponse;
import br.pucrs.ages.treinamentoautoguiado.api.util.ApiRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnswersServiceTest {

    @Mock
    private AnswersRepository answersRepository;
    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private AnswersService answersService;




    @Test
    void shouldReturnAnswersResponse() {
        Long userId = 1L;
        Long moduleId = 10L;
        Module module = Module.builder().name("Módulo Teste").build();


        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));

        ModuleItem moduleItem = ModuleItem.builder().module(module).build();
        Answer answer = Answer.builder().content("Resposta 1").modules_items(moduleItem).build();

        when(answersRepository.findByUserIdAndModuleItemModuleId(userId, moduleId))
                .thenReturn(List.of(answer));

        AnswersResponse responses = answersService.getAnswersByUserAndModule(userId, moduleId);

        assertThat(responses.getModuleName()).isEqualTo("Módulo Teste");
        assertThat(responses.getAnswers()).contains("Resposta 1");

        verify(answersRepository).findByUserIdAndModuleItemModuleId(userId, moduleId);
    }

    @Test
    void testGetAnswer_shouldThrowException_whenModuleDoesNotExist() {
        Long userId = 1L;
        Long moduleId = 10L;




        when(moduleRepository.findById(moduleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> answersService.getAnswersByUserAndModule(userId, moduleId))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Módulo não encontrado");

        verify(moduleRepository, times(1)).findById(moduleId);
        verifyNoMoreInteractions(answersRepository, moduleRepository);
    }
}
