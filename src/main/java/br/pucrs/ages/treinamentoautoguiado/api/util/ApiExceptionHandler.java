package br.pucrs.ages.treinamentoautoguiado.api.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ApiExceptionHandler {

    // Exceção personalizada ApiRequestException
    @ExceptionHandler(ApiRequestException.class)
    public ResponseEntity<Object> handle(ApiRequestException ex) {
        HttpStatus status;

        if (ex.getMessage().toLowerCase().contains("não encontrado")) {
            status = HttpStatus.NOT_FOUND;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }
        
        ApiException apiException = new ApiException(
                ex.getMessage(),
                formatCause(ex.getCause()),
                status,
                ZonedDateTime.now(ZoneId.systemDefault())
        );

        return new ResponseEntity<>(apiException, status);
    }

    // Exceção de validação de campos (MethodArgumentNotValidException)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        ApiException apiException = new ApiException(
                "Erro de validação",
                formatCause(ex.getCause()),
                badRequest,
                ZonedDateTime.now(ZoneId.systemDefault()),
                errors
        );

        return new ResponseEntity<>(apiException, badRequest);
    }

    // Exceção para 404
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handle404Method(HttpRequestMethodNotSupportedException ex) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;

        ApiException apiException = new ApiException(
                "A rota solicitada não foi encontrada.",
                formatCause(ex.getCause()),
                notFound,
                ZonedDateTime.now(ZoneId.systemDefault()),
                new ArrayList<>()
        );

        return new ResponseEntity<>(apiException, notFound);
    }

    // Exceção para 404
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handle404Route(NoHandlerFoundException ex) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;

        ApiException apiException = new ApiException(
                "A rota solicitada não foi encontrada.",
                formatCause(ex.getCause()),
                notFound,
                ZonedDateTime.now(ZoneId.systemDefault()),
                new ArrayList<>()
        );

        return new ResponseEntity<>(apiException, notFound);
    }

    // Exceção genérica para qualquer outra exceção não tratada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiException apiException = new ApiException(
                "Erro interno do servidor. Tente novamente mais tarde.",
                formatCause(ex.getCause()),
                status,
                ZonedDateTime.now(ZoneId.systemDefault())
        );

        return new ResponseEntity<>(apiException, status);
    }

    // Tratar erro de JSON inválido
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMessage = "Formato de dado inválido. Verifique se os valores enviados estão corretos.";

        ApiException apiException = new ApiException(
                errorMessage,
                formatCause(ex.getCause()),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now(ZoneId.systemDefault())
        );

        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }

    public Throwable formatCause(Throwable cause) {
        if (cause == null) return null;

        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        StackTraceElement[] stackTrace = cause.getStackTrace();
        if (stackTrace.length > 0) {
            StackTraceElement lastElement = stackTrace[stackTrace.length - 1];
            cause.setStackTrace(new StackTraceElement[]{lastElement});
        }

        return cause;
    }
}
