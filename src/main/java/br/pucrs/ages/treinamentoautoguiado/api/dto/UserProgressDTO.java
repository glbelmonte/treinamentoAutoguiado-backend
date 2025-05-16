package br.pucrs.ages.treinamentoautoguiado.api.dto;

import jakarta.validation.constraints.NotNull; 
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProgressDTO {

    @NotNull(message = "O id do usuário deve ser informado") 
    private Long userId;

    @NotNull(message = "O id do item do módulo deve ser informado") 
    private Long moduleItemId;
}
