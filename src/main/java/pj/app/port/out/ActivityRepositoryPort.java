package pj.app.port.out;

import pj.adapter.out.github.dto.GitHubEventDTO;
import java.util.List;

public interface ActivityRepositoryPort {
    void saveAll(List<GitHubEventDTO> events);
    void saveEvent(GitHubEventDTO event);
    void deleteEvent(String id);
    void deleteAllEvents();
}
