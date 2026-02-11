package pj.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "activity_history")
@Getter
@Setter
@NoArgsConstructor
public class ActivityHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String activity_type;
    private String repo_name;
    private Instant occurred_at;
    public ActivityHistoryEntity(String activity_type, String repo_name, Instant occurred_at) {
        this.activity_type = activity_type;
        this.repo_name = repo_name;
        this.occurred_at = occurred_at;
    }
}
