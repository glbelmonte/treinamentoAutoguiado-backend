package br.pucrs.ages.treinamentoautoguiado.api.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApiExceptionHandlerTest {

    @Mock
    private ApiRequestException apiRequestException;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException;

    @Mock
    private NoHandlerFoundException noHandlerFoundException;

    @Mock
    private HttpMessageNotReadableException httpMessageNotReadableException;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ApiExceptionHandler apiExceptionHandler;

    @Test
    void testHandle_ShouldReturnBadRequest() {
        String errorMessage = "Test";
        when(apiRequestException.getMessage()).thenReturn(errorMessage);
        when(apiRequestException.getCause()).thenReturn(new Throwable("Cause"));

        ResponseEntity<Object> responseEntity = apiExceptionHandler.handle(apiRequestException);

        ApiException apiException = (ApiException) responseEntity.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(apiException).getMessage());
    }

    @Test
    void testHandleValidationExceptions_ShouldReturnBadRequestWithFieldError() {
        FieldError fieldError1 = new FieldError("object", "field1", "Campo 1 inválido");
        FieldError fieldError2 = new FieldError("object", "field2", "Campo 2 inválido");
        List<FieldError> fieldErrors = Arrays.asList(fieldError1, fieldError2);

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ResponseEntity<Object> responseEntity = apiExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        ApiException apiException = (ApiException) responseEntity.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Erro de validação", Objects.requireNonNull(apiException).getMessage());

        List<String> expectedErrors = Arrays.asList("field1: Campo 1 inválido", "field2: Campo 2 inválido");
        assertEquals(expectedErrors, apiException.getErrors());
    }

    @Test
    void testHandle_ShouldReturnNotFound_WhenMessageContainsNaoEncontrado() {
        String errorMessage = "Usuário não encontrado";
        ApiRequestException exception = new ApiRequestException(errorMessage);

        ResponseEntity<Object> response = apiExceptionHandler.handle(exception);

        ApiException apiException = (ApiException) response.getBody();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, apiException.getMessage());
    }

    @Test
    void testHandle404_ShouldReturnNotFound_WhenHttpRequestMethodNotSupportedExceptionOccurs() {
        String errorMessage = "A rota solicitada não foi encontrada.";

        when(httpRequestMethodNotSupportedException.getCause()).thenReturn(new Throwable("Cause"));

        ResponseEntity<Object> responseEntity = apiExceptionHandler.handle404Method(httpRequestMethodNotSupportedException);

        ApiException apiException = (ApiException) responseEntity.getBody();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(apiException).getMessage());
    }

    @Test
    void testHandle404_ShouldReturnNotFound_WhenNoHandlerFoundExceptionOccurs() {
        String errorMessage = "A rota solicitada não foi encontrada.";

        when(noHandlerFoundException.getCause()).thenReturn(new Throwable("Cause"));

        ResponseEntity<Object> responseEntity = apiExceptionHandler.handle404Route(noHandlerFoundException);

        ApiException apiException = (ApiException) responseEntity.getBody();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(apiException).getMessage());
    }

    @Test
    void testHandleGenericException_ShouldReturnInternalServerError_WhenExceptionOccurs() {
        String errorMessage = "Erro interno do servidor. Tente novamente mais tarde.";
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<Object> responseEntity = apiExceptionHandler.handleGenericException(exception);

        ApiException apiException = (ApiException) responseEntity.getBody();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(apiException).getMessage());
    }

    @Test
    void testHandleGenericException_ShouldReturnInternalServerError_WhenHttpMessageNotReadableException() {
        String errorMessage = "Formato de dado inválido. Verifique se os valores enviados estão corretos.";
        when(httpMessageNotReadableException.getCause()).thenReturn(new Throwable("Cause"));

        ResponseEntity<Object> responseEntity = apiExceptionHandler.handleHttpMessageNotReadableException(httpMessageNotReadableException);

        ApiException apiException = (ApiException) responseEntity.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(apiException).getMessage());
    }

    @Test
    void testFormatCause_ShouldReturnNull_whenCauseIsNull() {
        Throwable result = apiExceptionHandler.formatCause(null);
        assertNull(result);
    }

    @Test
    void testFormatCause_ShouldReturnCauseWithTrance_whenCauseHasNoCause() {
        Throwable cause = new Throwable("Test exception");
        Throwable result = apiExceptionHandler.formatCause(cause);

        assertNotNull(result);
        assertEquals(cause, result);
        assertEquals(1, result.getStackTrace().length);
    }

    @Test
    void testFormatCause_ShouldReturnCauseWithoutTrance_whenStackTraceIsEmpty() {
        Throwable cause = new Throwable("Test exception with empty stack trace");
        cause.setStackTrace(new StackTraceElement[] {});

        Throwable result = apiExceptionHandler.formatCause(cause);

        assertNotNull(result);
        assertEquals(cause, result);
        assertEquals(0, result.getStackTrace().length);
    }

    @Test
    void testFormatCause_ShouldReturnCauseWithLastTrace_whenCauseHasStackTrace() {
        Throwable cause = new Throwable("Test exception with stack trace");
        cause.setStackTrace(new StackTraceElement[] {
                new StackTraceElement("className", "methodName", "fileName", 10),
                new StackTraceElement("className", "methodName", "fileName", 20)
        });

        Throwable result = apiExceptionHandler.formatCause(cause);

        assertNotNull(result);
        assertEquals(1, result.getStackTrace().length);
        assertEquals("className", result.getStackTrace()[0].getClassName());
        assertEquals("methodName", result.getStackTrace()[0].getMethodName());
        assertEquals(20, result.getStackTrace()[0].getLineNumber());
    }
}
