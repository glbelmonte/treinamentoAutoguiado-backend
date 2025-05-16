package br.pucrs.ages.treinamentoautoguiado.api.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class LoginUserDTO {

    @NotEmpty(message = "A senha deve ser informada")
    private String password;

    @NotEmpty(message = "O e-mail deve ser informado")
    private String email;

}
