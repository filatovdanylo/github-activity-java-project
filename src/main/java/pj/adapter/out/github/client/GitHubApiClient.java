package pj.adapter.out.github.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pj.app.port.out.FetchGithubEventsPort;
import pj.domain.model.Activity;
import pj.domain.model.ActivityType;
import pj.exceptions.GitHubApiException;
import pj.exceptions.RateLimitException;
import pj.exceptions.UserNotFoundException;
import pj.adapter.out.github.dto.GitHubEventDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GitHubApiClient implements FetchGithubEventsPort {
    private final RestClient restClient;
    private static final Logger logger = LoggerFactory.getLogger(GitHubApiClient.class);

    @Autowired
    public GitHubApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<Activity> fetchUserEvents(String username) {
        logger.info("Loading user events for username {} from GitHub", username);
        String url = "/users/" + username + "/events";

        try {
            List<GitHubEventDTO> events = restClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, ((_, response) -> {
                        switch (response.getStatusCode().value()) {
                            case 403, 429 -> throw new RateLimitException();
                            case 404 -> throw new UserNotFoundException(username);
                            default -> throw new GitHubApiException("Client error: HTTP " + response.getStatusCode());
                        }
                    }))
                    .onStatus(HttpStatusCode::is5xxServerError, ((_, response) -> {
                        throw new GitHubApiException("GitHub server error: HTTP " + response.getStatusCode());
                    }))
                    .body(new ParameterizedTypeReference<List<GitHubEventDTO>>() {
                    });
            logger.info("Got {} events from {}", events.size(), username);
            List<Activity> activities = events.stream().map(eventDTO -> new Activity(switch (eventDTO.type()) {
                case "PushEvent" -> ActivityType.PUSH;
                case "IssuesEvent" -> ActivityType.ISSUE_OPENED;
                case "WatchEvent" -> ActivityType.STARRED;
                case "PullRequestEvent" -> ActivityType.PULL_REQUEST;
                case "CreateEvent" -> ActivityType.CREATE;
                case "ForkEvent" -> ActivityType.FORK;
                default -> ActivityType.OTHER;
            }, eventDTO.repo().name(), eventDTO.createdAt())).collect(Collectors.toCollection(ArrayList::new));
            return activities;
        } catch (GitHubApiException e) {
            throw e;
        }
    }


}
