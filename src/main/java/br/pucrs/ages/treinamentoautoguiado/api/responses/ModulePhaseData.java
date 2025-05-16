package br.pucrs.ages.treinamentoautoguiado.api.responses;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModulePhaseData {
    private Integer phase;
    private String name;
    private String progress;

}
