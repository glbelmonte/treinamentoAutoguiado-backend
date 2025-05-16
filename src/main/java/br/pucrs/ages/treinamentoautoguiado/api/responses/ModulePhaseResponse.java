package br.pucrs.ages.treinamentoautoguiado.api.responses;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ModulePhaseResponse {
    private String progress;

    @JsonProperty("module_name")
    private String moduleName;

    private List<ModulePhaseData> data;
}
