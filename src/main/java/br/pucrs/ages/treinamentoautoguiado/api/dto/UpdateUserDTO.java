package br.pucrs.ages.treinamentoautoguiado.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {

    @JsonProperty(value = "is_first_access")
    private Boolean isFirstAccess;

}
