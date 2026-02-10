package pj.app.port.out;

import pj.domain.model.Activity;

import java.util.List;

public interface ActivityRepositoryPort {
    void saveAll(List<Activity> events);
    void saveEvent(Activity event);
    int getCountByRepo(String repo_name);
    void deleteEventsForRepo(String repo_name);
    void deleteAllEvents();
}
