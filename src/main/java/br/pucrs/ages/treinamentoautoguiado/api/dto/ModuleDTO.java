package br.pucrs.ages.treinamentoautoguiado.api.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ModuleDTO {
    private Long id;
    private Integer order;
}
