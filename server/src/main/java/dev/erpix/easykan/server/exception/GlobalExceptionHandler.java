package dev.erpix.easykan.server.exception;

import dev.erpix.easykan.server.exception.common.RestException;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			@NotNull HttpHeaders headers, @NotNull HttpStatusCode status, WebRequest request) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, "Validation failed for one or more fields.");
		problem.setTitle("Validation Error");
		problem.setInstance(URI.create(request.getDescription(false)));

		Map<String, String> errors = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.filter(field -> field.getDefaultMessage() != null)
			.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage,
					(existingValue, newValue) -> existingValue));

		problem.setProperty("errors", errors);

		return ResponseEntity.status(status).body(problem);
	}

	@ExceptionHandler(MissingRequestCookieException.class)
	public ResponseEntity<ProblemDetail> handleMissingCookie(MissingRequestCookieException ex, WebRequest request) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,
				"Required cookie '" + ex.getCookieName() + "' is missing.");
		problem.setTitle("Missing Cookie");
		problem.setInstance(URI.create(request.getDescription(false)));

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
	}

	@ExceptionHandler(RestException.class)
	public ResponseEntity<ProblemDetail> handleRestException(RestException ex, WebRequest request) {
		String message = ex.getMessage() != null ? ex.getMessage() : "Unexpected error";
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(ex.getStatus(), message);
		problem.setTitle(ex.getStatus().getReasonPhrase());
		problem.setInstance(URI.create(request.getDescription(false)));

		return ResponseEntity.status(ex.getStatus()).body(problem);
	}

}
