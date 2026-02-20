package pj.domain.model;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ActivityTest {

    @Test
    void shouldCreateActivity() {
        ActivityType activityType = ActivityType.CREATE;
        String repo = "user/repository";
        ZonedDateTime occurredAt = ZonedDateTime.now();

        Activity activity = new Activity(activityType, repo, occurredAt);

        assertNotNull(activity);
        assertEquals(activityType, activity.type());
        assertEquals(repo, activity.repositoryName());
        assertEquals(occurredAt, activity.occurredAt());
    }
}
