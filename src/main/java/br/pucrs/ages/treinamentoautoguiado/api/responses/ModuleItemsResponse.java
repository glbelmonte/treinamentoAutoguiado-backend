package br.pucrs.ages.treinamentoautoguiado.api.responses;

import br.pucrs.ages.treinamentoautoguiado.api.entity.ModuleItem;
import br.pucrs.ages.treinamentoautoguiado.api.model.ModuleItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class ModuleItemsResponse {
    private Long id;
    private String text;
    private Integer modulePhase;
    private ModuleItemType type;
    private Integer moduleItemOrder;
    private String value;

    public ModuleItemsResponse(ModuleItem moduleItem) {
        this.id = moduleItem.getId();
        this.text = moduleItem.getText();
        this.modulePhase = moduleItem.getModulePhase();
        this.type = moduleItem.getType();
        this.moduleItemOrder = moduleItem.getModuleItemOrder();
    }
}
