package com.a.demo.exception;

import com.a.demo.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test unitario directo del handler (sin Spring).
 * Probamos que cada metodo arme la ErrorResponse con el codigo correcto.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/foo");
    }

    @Test
    void handleNotFound_returns404WithMessage() {
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(
                new ResourceNotFoundException("Foo", 1L), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).isEqualTo("Foo not found with id: 1");
        assertThat(response.getBody().getPath()).isEqualTo("/api/foo");
    }

    @Test
    void handleCustomValidation_returns400WithErrors() {
        ValidationException ex = new ValidationException(List.of("endDate must be after startDate"));
        ResponseEntity<ErrorResponse> response = handler.handleCustomValidation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrors()).contains("endDate must be after startDate");
    }

    @Test
    void handleIllegalArgument_returns400() {
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(
                new IllegalArgumentException("limit must be positive"), request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("limit must be positive");
    }

    @Test
    void handleGeneric_returns500WithoutLeakingStackTrace() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(
                new RuntimeException("internal detail with secret token"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        // El mensaje al cliente NO debe contener el detalle interno
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().getMessage()).doesNotContain("secret");
    }
}
