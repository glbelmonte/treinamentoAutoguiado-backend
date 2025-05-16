package br.pucrs.ages.treinamentoautoguiado.api.repository;

import br.pucrs.ages.treinamentoautoguiado.api.entity.ModuleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleItemRepository extends JpaRepository<ModuleItem, Long> {

    List<ModuleItem> findByModuleIdOrderByModulePhaseAscModuleItemOrderAsc(Long moduleId);

}