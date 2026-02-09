package pj.adapter.out.github.dto;

import java.time.ZonedDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubEventDTO(String id,
                             String type,
                             @JsonProperty("created_at") ZonedDateTime createdAt,
                             Repository repo,
                             Map<String, Object> payload
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Repository(String name, String url) {
    }
}
