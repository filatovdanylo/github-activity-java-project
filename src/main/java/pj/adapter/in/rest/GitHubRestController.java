package pj.adapter.in.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pj.adapter.in.rest.dtos.ActivityResponseDto;
import pj.app.port.in.LoadUserActivityUseCase;
import pj.domain.model.Activity;
import pj.exceptions.GitHubApiException;
import pj.exceptions.RateLimitException;
import pj.exceptions.UserNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class GitHubRestController {
    private final LoadUserActivityUseCase useCase;
    private static final Logger logger = LoggerFactory.getLogger(GitHubRestController.class);

    public GitHubRestController(LoadUserActivityUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/{username}/activity")
    public ResponseEntity<ActivityResponseDto> getUserActivity(@PathVariable String username) {
        logger.info("Fetching activity for user: {}", username);

        try {
            List<Activity> activities = useCase.getUserActivity(username);

            ActivityResponseDto activityResponseDto = new ActivityResponseDto(
                    username,
                    activities.size(),
                    activities
            );

            return ResponseEntity.ok(activityResponseDto);

        } catch (UserNotFoundException e) {
            logger.warn("User not found: {}", username);
            throw e;
        } catch (RateLimitException e) {
            logger.warn("Rate limit exceeded: {}", e.getMessage());
            throw e;
        } catch (GitHubApiException e) {
            logger.warn("Github API error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
