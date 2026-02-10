package pj.adapter.out.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import pj.adapter.out.persistence.repo.JpaActivityRepository;
import pj.app.port.out.ActivityRepositoryPort;
import pj.domain.model.Activity;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ActivityRepositoryAdapter implements ActivityRepositoryPort {
    private final JpaActivityRepository repository;
    private final JpaActivityRepository jpaActivityRepository;

    public ActivityRepositoryAdapter(JpaActivityRepository jpaRepository, JpaActivityRepository jpaActivityRepository) {
        this.repository = jpaRepository;
        this.jpaActivityRepository = jpaActivityRepository;
    }

    @Override
    public void saveAll(List<Activity> events) {
        List<ActivityHistoryEntity> savings = events.stream()
                .map(event -> new ActivityHistoryEntity(
                        event.repositoryName(),
                        events.size(),
                        event.occurredAt().toInstant())
                ).toList();
        repository.saveAll(savings);
    }

    @Override
    public void saveEvent(Activity event) {
        repository.save(new ActivityHistoryEntity(
                event.repositoryName(),
                1,
                event.occurredAt().toInstant())
        );
    }

    @Override
    public int getCountByRepo(String repo_name) {
        return jpaActivityRepository.getNumberOfActivitiesForRepo(repo_name);
    }

    @Override
    public void deleteEventsForRepo(String repo_name) {
        repository.deleteByRepo_name(repo_name);
    }

    @Override
    public void deleteAllEvents() {
        repository.deleteAll();
    }
}
