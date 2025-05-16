package br.pucrs.ages.treinamentoautoguiado.api.service;

import br.pucrs.ages.treinamentoautoguiado.api.entity.Answer;
import br.pucrs.ages.treinamentoautoguiado.api.entity.Module;
import br.pucrs.ages.treinamentoautoguiado.api.entity.ModuleItem;
import br.pucrs.ages.treinamentoautoguiado.api.model.ModuleItemType;
import br.pucrs.ages.treinamentoautoguiado.api.repository.AnswerRepository;
import br.pucrs.ages.treinamentoautoguiado.api.repository.ModuleItemRepository;
import br.pucrs.ages.treinamentoautoguiado.api.repository.ModuleRepository;
import br.pucrs.ages.treinamentoautoguiado.api.repository.UserProgressRepository;
import br.pucrs.ages.treinamentoautoguiado.api.responses.ModuleItemsResponse;
import br.pucrs.ages.treinamentoautoguiado.api.responses.ModulePhaseResponse;
import br.pucrs.ages.treinamentoautoguiado.api.responses.ModuleResponse;
import br.pucrs.ages.treinamentoautoguiado.api.responses.ModuleResponseData;
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
public class ModuleServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private UserProgressRepository userProgressRepository;

    @Mock
    private ModuleItemRepository moduleItemRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private ModuleService moduleService;

    @Test
    void testGetModulePhases_shouldReturnModulePhases_whenModuleExists() {
        Module module = mock(Module.class);
        when(module.getName()).thenReturn("Módulo Teste");

        ModuleItem item1 = mock(ModuleItem.class);
        when(item1.getModulePhase()).thenReturn(1);

        ModuleItem item2 = mock(ModuleItem.class);
        when(item2.getModulePhase()).thenReturn(2);

        ModuleItem item3 = mock(ModuleItem.class);
        when(item3.getModulePhase()).thenReturn(3);

        when(userProgressRepository.findCompletedModuleItemIdsByUserId(any())).thenReturn(List.of());
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(module));
        when(moduleItemRepository.findByModuleIdOrderByModulePhaseAscModuleItemOrderAsc(1L))
                .thenReturn(List.of(item1, item2, item3));

        ModulePhaseResponse response = moduleService.getModulePhases(1L, 1001L);

        assertThat(response).isNotNull();
        assertThat(response.getProgress()).isEqualTo("0%");
        assertThat(response.getModuleName()).isEqualTo("Módulo Teste");
        assertThat(response.getData()).hasSize(3);
        assertThat(response.getData().get(0).getPhase()).isEqualTo(1);
        assertThat(response.getData().get(0).getName()).isEqualTo("Fase 1");
        assertThat(response.getData().get(0).getProgress()).isEqualTo("0%");
        assertThat(response.getData().get(1).getPhase()).isEqualTo(2);
        assertThat(response.getData().get(1).getName()).isEqualTo("Fase 2");
        assertThat(response.getData().get(1).getProgress()).isEqualTo("0%");


        verify(moduleRepository, times(1)).findById(1L);
        verify(moduleItemRepository, times(1))
                .findByModuleIdOrderByModulePhaseAscModuleItemOrderAsc(1L);
    }

    @Test
    void testGetModulePhases_shouldThrowException_whenModuleDoesNotExist() {
        when(moduleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moduleService.getModulePhases(99L, 1001L))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Módulo não encontrado");

        verify(moduleRepository, times(1)).findById(99L);
        verify(moduleItemRepository, never())
                .findByModuleIdOrderByModulePhaseAscModuleItemOrderAsc(anyLong());
    }

    @Test
    void testGetModulePhases_shouldHandleEmptyPhases_whenNoItems() {
        Module module = mock(Module.class);
        when(module.getName()).thenReturn("Módulo Teste");

        when(moduleRepository.findById(1L)).thenReturn(Optional.of(module));
        when(moduleItemRepository.findByModuleIdOrderByModulePhaseAscModuleItemOrderAsc(1L))
                .thenReturn(List.of());

        ModulePhaseResponse response = moduleService.getModulePhases(1L, 1001L);

        assertThat(response).isNotNull();
        assertThat(response.getProgress()).isEqualTo("0%");
        assertThat(response.getModuleName()).isEqualTo("Módulo Teste");
        assertThat(response.getData()).isEmpty();

        verify(moduleRepository, times(1)).findById(1L);
        verify(moduleItemRepository, times(1))
                .findByModuleIdOrderByModulePhaseAscModuleItemOrderAsc(1L);
    }
    @Test
void testGetAllModules_shouldReturnAllModulesWithProgress() {
    Module module1 = mock(Module.class);
    when(module1.getId()).thenReturn(1L);
    when(module1.getName()).thenReturn("Módulo 1");
    when(module1.getOrder()).thenReturn(1);
    when(module1.getProgress()).thenReturn("50%");

    Module module2 = mock(Module.class);
    when(module2.getId()).thenReturn(2L);
    when(module2.getName()).thenReturn("Módulo 2");
    when(module2.getOrder()).thenReturn(2);
    when(module2.getProgress()).thenReturn("100%");

    when(moduleRepository.findAll()).thenReturn(List.of(module1, module2));

    ModuleResponse response = moduleService.getAllModules(123L);

    assertThat(response).isNotNull();
    assertThat(response.getProgress()).isEqualTo("0%"); // pois o cálculo ainda retorna "0%"
    assertThat(response.getData()).hasSize(2);

    ModuleResponseData first = response.getData().get(0);
    assertThat(first.getId()).isEqualTo(1L);
    assertThat(first.getName()).isEqualTo("Módulo 1");
    assertThat(first.getOrder()).isEqualTo(1);
    assertThat(first.getProgress()).isEqualTo("50%");

    ModuleResponseData second = response.getData().get(1);
    assertThat(second.getId()).isEqualTo(2L);
    assertThat(second.getName()).isEqualTo("Módulo 2");
    assertThat(second.getOrder()).isEqualTo(2);
    assertThat(second.getProgress()).isEqualTo("100%");

    verify(moduleRepository, times(1)).findAll();
}
@Test
void testGetAllModules_shouldHandleEmptyModuleList() {
    when(moduleRepository.findAll()).thenReturn(List.of());

    ModuleResponse response = moduleService.getAllModules(123L);

    assertThat(response).isNotNull();
    assertThat(response.getProgress()).isEqualTo("0%");
    assertThat(response.getData()).isEmpty();

    verify(moduleRepository, times(1)).findAll();
}

    @Test
    void testFetchPhaseModuleItems_shouldThrowException_whenModuleDoesNotExist() {
        when(moduleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moduleService.fetchPhaseModuleItems(99L, 1, 1001L))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Módulo não encontrado");

        verify(moduleRepository, times(1)).findById(99L);
        verify(answerRepository, never())
                .findByModuleItemIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void testFetchPhaseModuleItems_shouldReturnModuleItems_whenModuleDoesExist() {

        ModuleItem item1 = mock(ModuleItem.class);
        when(item1.getModulePhase()).thenReturn(1);
        when(item1.getId()).thenReturn(1L);
        when(item1.getText()).thenReturn("text1");
        when(item1.getType()).thenReturn(ModuleItemType.TEXT);
        when(item1.getModuleItemOrder()).thenReturn(1);

        ModuleItem item2 = mock(ModuleItem.class);
        when(item2.getModulePhase()).thenReturn(2);


        Module module = mock(Module.class);
        when(module.getModuleItems()).thenReturn(List.of(item1, item2));

        Answer answer = mock(Answer.class);
        when(answer.getContent()).thenReturn("respostaContent");

        when(moduleRepository.findById(1L)).thenReturn(Optional.of(module));
        when(answerRepository.findByModuleItemIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(answer));

        List<ModuleItemsResponse> response = moduleService.fetchPhaseModuleItems(1L, 1, 1001L);

        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getModulePhase()).isEqualTo(1);
        assertThat(response.get(0).getType()).isEqualTo(ModuleItemType.TEXT);
        assertThat(response.get(0).getText()).isEqualTo("text1");
        assertThat(response.get(0).getModuleItemOrder()).isEqualTo(1);
        assertThat(response.get(0).getValue()).isEqualTo("respostaContent");


        verify(moduleRepository, times(1)).findById(1L);
        verify(answerRepository, times(1))
                .findByModuleItemIdAndUserId(anyLong(), anyLong());
    }
}