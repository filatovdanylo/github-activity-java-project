package pj.adapter.in.rest.dtos;

import java.time.LocalDateTime;

public record ErrorMessage(int status, String error, String message, LocalDateTime timestamp) {
}
