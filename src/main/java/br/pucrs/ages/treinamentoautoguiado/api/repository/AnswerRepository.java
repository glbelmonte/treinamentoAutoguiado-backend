package br.pucrs.ages.treinamentoautoguiado.api.repository;

import br.pucrs.ages.treinamentoautoguiado.api.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query ("SELECT a FROM answers a WHERE a.modules_items.id = :moduleItemId AND a.user.id = :userId")
    Optional<Answer> findByModuleItemIdAndUserId(Long moduleItemId, Long userId);
}
