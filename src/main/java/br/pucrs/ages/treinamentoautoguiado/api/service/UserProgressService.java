package br.pucrs.ages.treinamentoautoguiado.api.service;

import org.springframework.stereotype.Service;

import br.pucrs.ages.treinamentoautoguiado.api.dto.UserProgressDTO;
import br.pucrs.ages.treinamentoautoguiado.api.entity.UserProgress;
import br.pucrs.ages.treinamentoautoguiado.api.repository.ModuleItemRepository;
import br.pucrs.ages.treinamentoautoguiado.api.repository.UserProgressRepository;
import br.pucrs.ages.treinamentoautoguiado.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import br.pucrs.ages.treinamentoautoguiado.api.util.ApiRequestException;
import br.pucrs.ages.treinamentoautoguiado.api.entity.ModuleItem;
import br.pucrs.ages.treinamentoautoguiado.api.entity.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProgressService {
    private final UserProgressRepository userProgressRepository; 
    private final ModuleItemRepository moduleItemRepository;
    private final UserRepository userRepository;

    public void saveUserProgress(UserProgressDTO userProgressDTO) {
        try {
            List<UserProgress> userProgress = userProgressRepository.findUserProgressByUserIdAndModuleItemId
                    (userProgressDTO.getUserId(), userProgressDTO.getModuleItemId());
            if (!userProgress.isEmpty()) return;

            ModuleItem moduleItem = moduleItemRepository.findById(userProgressDTO.getModuleItemId())
                    .orElseThrow(() -> new ApiRequestException("Item de módulo não encontrado"));

            User user = userRepository.findById(userProgressDTO.getUserId())
                    .orElseThrow(() -> new ApiRequestException("Usuário não encontrado"));

            userProgressRepository.save(new UserProgress(moduleItem, user));
        } catch (ApiRequestException e) { 
            throw e; 
        } catch (Exception e) { 
            throw new RuntimeException("Erro ao salvar progresso do usuário", e); 
        }
    }    
}