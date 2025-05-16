package br.pucrs.ages.treinamentoautoguiado.api.repository;

import br.pucrs.ages.treinamentoautoguiado.api.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnswersRepository extends JpaRepository<Answer, Long> {
    @Query("SELECT a FROM answers a WHERE a.user.id = :userId AND a.modules_items.module.id = :moduleId")
    List<Answer> findByUserIdAndModuleItemModuleId(Long userId, Long moduleId);
}
