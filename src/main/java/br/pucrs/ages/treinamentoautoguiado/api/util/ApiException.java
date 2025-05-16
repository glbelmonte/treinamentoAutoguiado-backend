package br.pucrs.ages.treinamentoautoguiado.api.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ApiException {

    private String message;
    private Throwable cause;
    private HttpStatus status;
    private ZonedDateTime expiresAt;
    private List<String> errors;

    public ApiException(String message, Throwable cause, HttpStatus badRequest, ZonedDateTime now) {
        this.message = message;
        this.cause = cause;
        this.status = badRequest;
        this.expiresAt = now;
    }
}
