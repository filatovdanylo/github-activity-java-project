package pj.exceptions;

public class RateLimitException extends RuntimeException {
    public RateLimitException() {
        super("GitHub API rate limit exceeded");
    }

    public RateLimitException(String resetTime) {
        super("Rate limit exceeded. Resets at: " + resetTime);
    }
}
