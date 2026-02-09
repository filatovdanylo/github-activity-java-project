package pj.adapter.in.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pj.adapter.in.rest.dtos.ErrorMessage;
import pj.exceptions.GitHubApiException;
import pj.exceptions.RateLimitException;
import pj.exceptions.UserNotFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleUserNotFound(UserNotFoundException ex) {

        ErrorMessage error = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                "User not found",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ErrorMessage> handleRateLimit(RateLimitException ex) {

        ErrorMessage error = new ErrorMessage(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "Rate limit exceeded",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
    }

    @ExceptionHandler(GitHubApiException.class)
    public ResponseEntity<ErrorMessage> handleGenericException(Exception ex) {

        ErrorMessage error = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                "An unexpected error occurred",
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
