package br.pucrs.ages.treinamentoautoguiado.api.util;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiExceptionTest {

    @Test
    void testApiException_ShouldConstructWithAllFields() {
        String message = "Test";
        Throwable cause = new RuntimeException("Cause");
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ZonedDateTime now = ZonedDateTime.now();
        List<String> errors = List.of("xxxxx", "zzzzz");

        ApiException apiException = new ApiException(message, cause, status, now, errors);

        assertEquals(message, apiException.getMessage());
        assertEquals(cause, apiException.getCause());
        assertEquals(status, apiException.getStatus());
        assertEquals(now, apiException.getExpiresAt());
        assertEquals(errors, apiException.getErrors());
    }

    @Test
    void testApiException_ShouldConstructWithPartialFields() {
        String message = "Test";
        Throwable cause = new RuntimeException("cause");
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ZonedDateTime now = ZonedDateTime.now();

        ApiException apiException = new ApiException(message, cause, status, now);

        assertEquals(message, apiException.getMessage());
        assertEquals(cause, apiException.getCause());
        assertEquals(status, apiException.getStatus());
        assertEquals(now, apiException.getExpiresAt());
    }
}
