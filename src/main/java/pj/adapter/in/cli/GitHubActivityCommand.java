package pj.adapter.in.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pj.app.port.in.LoadUserActivityUseCase;
import pj.adapter.out.github.formatter.GitHubConsoleFormatter;
import pj.domain.model.Activity;
import pj.exceptions.*;

import java.util.List;

@Component
public class GitHubActivityCommand {

    private final LoadUserActivityUseCase useCase;
    private final GitHubConsoleFormatter formatter;
    private static final Logger logger = LoggerFactory.getLogger(GitHubActivityCommand.class);

    public GitHubActivityCommand(LoadUserActivityUseCase useCase,
                                 GitHubConsoleFormatter formatter) {
        this.useCase = useCase;
        this.formatter = formatter;
    }

    public void execute(String username) {
        System.out.println("Fetching activity for: " + username);

        try {
            List<Activity> activities = useCase.getUserActivity(username);
            String output = formatter.format(activities);
            System.out.println(output);

        } catch (UserNotFoundException e) {
            logger.warn("User not found: {}", username);
            System.err.println("Error: User '" + username + "' not found");
            System.exit(1);

        } catch (RateLimitException e) {
            logger.warn("Rate limit exceeded: {}", e.getMessage());
            System.err.println("Error: GitHub API rate limit exceeded. Please try again later.");
            System.exit(1);

        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
