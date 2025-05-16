package br.pucrs.ages.treinamentoautoguiado.api.responses;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ModuleResponseData {
    private Long id;
    private String name;
    private Integer order;
    private String progress;
}
