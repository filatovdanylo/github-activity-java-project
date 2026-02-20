package pj.app.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pj.app.port.out.FetchGithubEventsPort;
import pj.domain.model.Activity;
import pj.domain.model.ActivityType;
import pj.exceptions.GitHubApiException;
import pj.exceptions.UserNotFoundException;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GitHubActivityServiceTest {

    @Mock
    private FetchGithubEventsPort gitHubApiClient;
    @InjectMocks
    private GitHubActivityService service;

    @Test
    void shouldReturnActivitiesWhenUserExists() {
        // Given
        String username = "testuser";
        List<Activity> expectedActivities = List.of(
                new Activity(ActivityType.PUSH, "user/repo", ZonedDateTime.now()),
                new Activity(ActivityType.STARRED, "user/repo2", ZonedDateTime.now())
        );

        when(gitHubApiClient.fetchUserEvents(username)).thenReturn(expectedActivities);

        // When
        List<Activity> result = service.getUserActivity(username);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ActivityType.PUSH, result.getFirst().type());
        verify(gitHubApiClient, times(1)).fetchUserEvents(username);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        String username = "nonexistentuser";
        when(gitHubApiClient.fetchUserEvents(username)).thenThrow(new UserNotFoundException(username));
        assertThrows(UserNotFoundException.class,
                () -> service.getUserActivity(username));
        verify(gitHubApiClient, times(1)).fetchUserEvents(username);
    }

    @Test
    void shouldThrowExceptionWhenGitHubFails() {
        String username = "testuser";
        when(gitHubApiClient.fetchUserEvents(username)).thenThrow(new GitHubApiException("API Exception"));
        assertThrows(GitHubApiException.class,
                () -> service.getUserActivity(username));
        verify(gitHubApiClient, times(1)).fetchUserEvents(username);
    }
}
