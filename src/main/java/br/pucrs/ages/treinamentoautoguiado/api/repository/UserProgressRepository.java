package br.pucrs.ages.treinamentoautoguiado.api.repository;
import br.pucrs.ages.treinamentoautoguiado.api.entity.UserProgress;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    @Query("SELECT up.module_item.id FROM user_progresses up WHERE up.user.id = :userId")
    List<Long> findCompletedModuleItemIdsByUserId(Long userId);

    @Query("SELECT up FROM user_progresses up WHERE up.user.id = :userId AND up.module_item.id = :moduleItemId")
    List<UserProgress> findUserProgressByUserIdAndModuleItemId(Long userId, Long moduleItemId);
}
