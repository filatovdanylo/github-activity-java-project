package pj.app.port.in;

import pj.adapter.out.github.dto.GitHubEventDTO;
import pj.domain.model.Activity;

import java.util.List;

public interface LoadUserActivityUseCase {
    /**
     * Retrieves the recent activity for a given GitHub user
     *
     * @param username the GitHub username
     * @return list of activities
     * @throws pj.exceptions.UserNotFoundException if user doesn't exist
     * @throws pj.exceptions.RateLimitException if API rate limit is exceeded
     */
    List<Activity> getUserActivity(String username);
}
