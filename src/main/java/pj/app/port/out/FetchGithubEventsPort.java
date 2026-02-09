package pj.app.port.out;

import pj.adapter.out.github.dto.GitHubEventDTO;
import pj.domain.model.Activity;
import pj.domain.model.ActivityType;

import java.util.List;

public interface FetchGithubEventsPort {
    List<Activity> fetchUserEvents(String username);
}
