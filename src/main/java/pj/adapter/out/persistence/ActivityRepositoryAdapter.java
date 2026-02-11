package pj.adapter.out.persistence;

import org.springframework.stereotype.Component;
import pj.adapter.out.persistence.mapper.ActivityToEntityMapper;
import pj.adapter.out.persistence.repo.JpaActivityRepository;
import pj.app.port.out.ActivityRepositoryPort;
import pj.domain.model.Activity;

import java.util.List;

@Component
public class ActivityRepositoryAdapter implements ActivityRepositoryPort {
    private final JpaActivityRepository repository;

    public ActivityRepositoryAdapter(JpaActivityRepository jpaRepository) {
        this.repository = jpaRepository;
    }

    @Override
    public void saveAll(List<Activity> events) {
        List<ActivityHistoryEntity> savings = events.stream()
                .map(ActivityToEntityMapper::apply)
                .toList();
        repository.saveAll(savings);
    }

    @Override
    public void saveEvent(Activity event) {
        repository.save(ActivityToEntityMapper.apply(event));
    }

    @Override
    public int getCountByRepo(String repo_name) {
        return repository.getNumberOfActivitiesForRepo(repo_name);
    }

    @Override
    public void deleteEventsForRepo(String repo_name) {
        repository.deleteByRepoName(repo_name);
    }

    @Override
    public void deleteAllEvents() {
        repository.deleteAll();
    }
}
