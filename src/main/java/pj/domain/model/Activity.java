package pj.domain.model;

import java.time.ZonedDateTime;

public record Activity(
        ActivityType type,
        String repositoryName,
        ZonedDateTime occurredAt
) {}
