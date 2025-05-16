package br.pucrs.ages.treinamentoautoguiado.api.service;

import br.pucrs.ages.treinamentoautoguiado.api.entity.Answer;
import br.pucrs.ages.treinamentoautoguiado.api.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.pucrs.ages.treinamentoautoguiado.api.entity.Module;
import br.pucrs.ages.treinamentoautoguiado.api.entity.ModuleItem;
import br.pucrs.ages.treinamentoautoguiado.api.repository.ModuleItemRepository;
import br.pucrs.ages.treinamentoautoguiado.api.repository.ModuleRepository;
import br.pucrs.ages.treinamentoautoguiado.api.repository.UserProgressRepository;
import br.pucrs.ages.treinamentoautoguiado.api.responses.ModuleResponse;
import br.pucrs.ages.treinamentoautoguiado.api.responses.ModuleResponseData;
import br.pucrs.ages.treinamentoautoguiado.api.responses.ModulePhaseData;
import br.pucrs.ages.treinamentoautoguiado.api.responses.ModulePhaseResponse;
import br.pucrs.ages.treinamentoautoguiado.api.responses.ModuleItemsResponse;
import br.pucrs.ages.treinamentoautoguiado.api.util.ApiRequestException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final ModuleItemRepository moduleItemRepository;
    private final UserProgressRepository userProgressRepository;
    private final AnswerRepository answerRepository;


    public ModuleResponse getAllModules(Long userId) {

        List<Module> modules = moduleRepository.findAll();
        List<ModuleResponseData> moduleData = new ArrayList<>();

        List<Long> completedItemIds = userProgressRepository.findCompletedModuleItemIdsByUserId(userId);
        
        int totalCompleted = 0;
        int totalItems = 0;

        for (Module module : modules) {
            List<ModuleItem> items = module.getModuleItems();
            int total = items.size();
            int completed = (int) items.stream()
                    .filter(item -> completedItemIds.contains(item.getId()))
                    .count();
            String progress = (total == 0) ? "0%" : (completed * 100 / total) + "%";

            module.setProgress(progress);
            totalCompleted += completed;
            totalItems += total;

            moduleData.add(ModuleResponseData.builder()
                    .id(module.getId())
                    .name(module.getName())
                    .order(module.getOrder())
                    .progress(module.getProgress())
                    .build());
        }

        String totalProgress = (totalItems == 0) ? "0%" : (100 * totalCompleted / totalItems) + "%";

        return new ModuleResponse(totalProgress, moduleData);
    }


    public ModulePhaseResponse getModulePhases(Long moduleId, Long userId) {

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ApiRequestException("Módulo não encontrado"));

        List<ModuleItem> moduleItems = moduleItemRepository
                .findByModuleIdOrderByModulePhaseAscModuleItemOrderAsc(moduleId);

        List<Long> completedItemIds = userProgressRepository.findCompletedModuleItemIdsByUserId(userId);

        Map<Integer, List<ModuleItem>> itemsByPhase = moduleItems.stream()
                .collect(Collectors.groupingBy(ModuleItem::getModulePhase));

        List<ModulePhaseData> phaseDataList = new ArrayList<>();

        int totalCompleted = 0;
        int totalItems = 0;

        for (Map.Entry<Integer, List<ModuleItem>> entry : itemsByPhase.entrySet()) {
            Integer phase = entry.getKey();

            List<ModuleItem> phaseItems = entry.getValue();

            int total = phaseItems.size();
            int completed = (int) phaseItems.stream()
                .filter(item -> completedItemIds.contains(item.getId()))
                .count();
            
            String phaseProgress = (total == 0) ? "0%" : (100 * completed / total) + "%";

            totalCompleted += completed;
            totalItems += total;

            String title = "Fase " + phase;

            phaseDataList.add(new ModulePhaseData(phase, title, phaseProgress));
        }

        String moduleProgress = (totalItems == 0) ? "0%" : (100 * totalCompleted / totalItems) + "%";

        return new ModulePhaseResponse(moduleProgress, module.getName(), phaseDataList);
    }

    public List<ModuleItemsResponse> fetchPhaseModuleItems(Long moduleId, Integer phase, Long userId){
        Module module = moduleRepository.findById(moduleId).orElseThrow(() -> new ApiRequestException("Módulo não encontrado"));
        List<ModuleItemsResponse> moduleItemsResponse = new ArrayList<>();
        List<ModuleItem> moduleItems = module.getModuleItems().stream().filter(item -> item.getModulePhase() == phase).toList();

        for(ModuleItem mi : moduleItems){
            Optional<Answer> resposta = answerRepository.findByModuleItemIdAndUserId(mi.getId(), userId);
            ModuleItemsResponse mir = new ModuleItemsResponse(mi);
            resposta.ifPresent(answer -> mir.setValue(answer.getContent()));
            moduleItemsResponse.add(mir);

        }
        return moduleItemsResponse;
    }
}
