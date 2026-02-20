package pj.adapter.out.github.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import pj.adapter.out.github.dto.GitHubEventDTO;
import pj.domain.model.Activity;
import pj.domain.model.ActivityType;
import pj.exceptions.GitHubApiException;
import pj.exceptions.UserNotFoundException;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GitHubApiClientTest {
    @Mock
    private RestClient restClient;
    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    private GitHubApiClient client;

    @BeforeEach
    void setUp() {
        client = new GitHubApiClient(restClient);
    }

    @Test
    void shouldMapGitHubEventsToActivities() {

        String username = "testuser";
        GitHubEventDTO pushEvent = new GitHubEventDTO(
                "1",
                "PushEvent",
                ZonedDateTime.now(),
                new GitHubEventDTO.Repository("repo", "//"),
                null
        );
        GitHubEventDTO starEvent = new GitHubEventDTO(
                "2",
                "WatchEvent",
                ZonedDateTime.now(),
                new GitHubEventDTO.Repository("repo1", "//"),
                null
        );
        List<GitHubEventDTO> events = List.of(pushEvent, starEvent);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(events);


        List<Activity> result = client.fetchUserEvents(username);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ActivityType.PUSH, result.getFirst().type());
        assertEquals(ActivityType.STARRED, result.get(1).type());
        assertEquals("repo", result.getFirst().repositoryName());
        assertEquals("repo1", result.get(1).repositoryName());

        verify(restClient, times(1)).get();

    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        String username = "nonexistentuser";

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenThrow(new UserNotFoundException(username));

        assertThrows(UserNotFoundException.class, () -> client.fetchUserEvents(username));

        verify(restClient, times(1)).get();

    }

    @Test
    void shouldReturnEmptyListWhenNoRecentActivity() {
        String username = "testuser";

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        List<GitHubEventDTO> emptyList = Collections.emptyList();
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(emptyList);

        List<Activity> result = client.fetchUserEvents(username);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(restClient, times(1)).get();
    }

    @Test
    void shouldThrowGeneralExceptionWhenServerDoesNotRespond() {
        String username = "userwithbrokenserver";
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenThrow(new GitHubApiException("Server is down"));

        assertThrows(GitHubApiException.class, () -> client.fetchUserEvents(username));

        verify(restClient, times(1)).get();
    }
}