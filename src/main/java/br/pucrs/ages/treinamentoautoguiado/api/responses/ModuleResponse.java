package br.pucrs.ages.treinamentoautoguiado.api.responses;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
public class ModuleResponse {
    private String progress;
    private List<ModuleResponseData> data;

    public ModuleResponse(String progress, List<ModuleResponseData> listaDeModulos) {
        List<ModuleResponseData> datas = new ArrayList<>();
        for (ModuleResponseData modulo : listaDeModulos) {
            datas.add(ModuleResponseData.builder()
                    .id(modulo.getId())
                    .name(modulo.getName())
                    .order(modulo.getOrder())
                    .progress(modulo.getProgress())
                    .build());
        }
        this.progress = progress;
        this.data = datas;
    }
}

