package br.pucrs.ages.treinamentoautoguiado.api.repository;

import br.pucrs.ages.treinamentoautoguiado.api.entity.Answer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AnswersRepositoryTest {

    @Autowired
    private AnswersRepository answersRepository;

    @Test
    void shouldFindAnswersByUserIdAndModuleId() {
        List<Answer> results = answersRepository.findByUserIdAndModuleItemModuleId(1L, 10L);
        assertThat(results).isNotNull();
    }
}
