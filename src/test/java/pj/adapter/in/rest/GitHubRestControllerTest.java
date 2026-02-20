package pj.adapter.in.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pj.app.port.in.LoadUserActivityUseCase;
import pj.domain.model.Activity;
import pj.domain.model.ActivityType;
import pj.exceptions.RateLimitException;
import pj.exceptions.UserNotFoundException;

import java.time.ZonedDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(GitHubRestController.class)
public class GitHubRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoadUserActivityUseCase useCase;

    @Test
    void shouldReturnActivitiesWhenUserExists() throws Exception {
        String username = "testuser";
        List<Activity> activities = List.of(
                new Activity(ActivityType.FORK, "repo", ZonedDateTime.now()),
                new Activity(ActivityType.CREATE, "repo1", ZonedDateTime.now())
        );

        when(useCase.getUserActivity(username)).thenReturn(activities);

        mockMvc.perform(get("/api/users/{username}/activity", username)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activityCount").value(2))
                .andExpect(jsonPath("$.activities[0].type").value("FORK"))
                .andExpect(jsonPath("$.activities[1].type").value("CREATE"));
    }

    @Test
    void shouldReturnErrorWhenUserDoesNotExist() throws Exception {
        String username = "nonexistentuser";

        when(useCase.getUserActivity(username)).thenThrow(new UserNotFoundException(username));

        mockMvc.perform(get("/api/users/{username}/activity", username)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnErrorWhenTooManyRequests() throws Exception {
        String username = "testuser";

        when(useCase.getUserActivity(username)).thenThrow(new RateLimitException());

        mockMvc.perform(get("/api/users/{username}/activity", username)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void shouldReturnInternalServerErrorWhenUnknownError() throws Exception {
        String username = "testuser";

        when(useCase.getUserActivity(username)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/users/{username}/activity", username)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
