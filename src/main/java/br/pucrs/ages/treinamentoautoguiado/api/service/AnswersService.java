package br.pucrs.ages.treinamentoautoguiado.api.service;

import br.pucrs.ages.treinamentoautoguiado.api.entity.Answer;
import br.pucrs.ages.treinamentoautoguiado.api.entity.Module;
import br.pucrs.ages.treinamentoautoguiado.api.repository.AnswersRepository;
import br.pucrs.ages.treinamentoautoguiado.api.repository.ModuleRepository;
import br.pucrs.ages.treinamentoautoguiado.api.responses.AnswersResponse;
import br.pucrs.ages.treinamentoautoguiado.api.util.ApiRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class AnswersService {

    private final ModuleRepository moduleRepository;

    private final AnswersRepository answersRepository;

    public AnswersResponse getAnswersByUserAndModule(Long userId, Long moduleId) {

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ApiRequestException("Módulo não encontrado"));


        List<Answer> answers = answersRepository.findByUserIdAndModuleItemModuleId(userId, moduleId);

        String moduleName = module.getName();

        List<String> contents = answers.stream().map(Answer::getContent).toList();

            return new AnswersResponse(moduleName, contents);
    }
}
