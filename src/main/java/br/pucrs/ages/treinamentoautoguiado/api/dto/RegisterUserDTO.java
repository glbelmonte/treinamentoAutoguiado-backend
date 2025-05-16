package br.pucrs.ages.treinamentoautoguiado.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class RegisterUserDTO {

    @NotEmpty(message = "A senha deve ser informada")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    private String password;

    @NotEmpty(message = "A confirmação da senha deve ser informada")
    private String matchingPassword;

    @NotEmpty(message = "O e-mail deve ser informado")
    @Email(message = "O e-mail informado é inválido")
    @Size(max = 100, message = "O e-mail não pode ter mais de 100 caracteres")
    private String email;

    @NotEmpty(message = "O nome deve ser informado")
    private String nome;

    @NotEmpty(message =  "O cpf deve ser informado")
    private String cpf;

    @AssertTrue(message = "As senhas não correspondem")
    @JsonIgnore
    public boolean isPasswordsMatching() {
        return password != null && password.equals(matchingPassword);
    }

    @AssertTrue(message = "O CPF informado é inválido")
    @JsonIgnore
    public boolean isCpfValido() {
        if (cpf == null || cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += (cpf.charAt(i) - '0') * (10 - i);
            }

            int firstCheckDigit = 11 - (sum % 11);
            if (firstCheckDigit >= 10) firstCheckDigit = 0;

            if (firstCheckDigit != (cpf.charAt(9) - '0')) {
                return false;
            }

            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += (cpf.charAt(i) - '0') * (11 - i);
            }

            int secondCheckDigit = 11 - (sum % 11);
            if (secondCheckDigit >= 10) secondCheckDigit = 0;

            return secondCheckDigit == (cpf.charAt(10) - '0');
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
