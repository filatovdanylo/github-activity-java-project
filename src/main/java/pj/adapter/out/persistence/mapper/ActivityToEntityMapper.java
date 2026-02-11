package pj.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;
import pj.adapter.out.persistence.ActivityHistoryEntity;
import pj.domain.model.Activity;

@Component
public class ActivityToEntityMapper {
    public static ActivityHistoryEntity apply(Activity activity) {
        return new ActivityHistoryEntity(
                activity.type().toString(),
                activity.repositoryName(),
                activity.occurredAt().toInstant()
        );
    }
}
