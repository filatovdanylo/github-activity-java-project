package pj.exceptions;

public class UserNotFoundException extends GitHubApiException {
    private final String username;

    public UserNotFoundException(String username) {
        super("User not found: " + username);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
