package br.pucrs.ages.treinamentoautoguiado.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswersResponse {

    @JsonProperty("module_name")
    private String moduleName;

    private List<String> answers;

}
