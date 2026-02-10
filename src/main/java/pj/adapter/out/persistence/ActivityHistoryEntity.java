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
    private String repo_name;
    private int number_of_activities;
    private Instant fetched_at;
    public ActivityHistoryEntity(String repo_name, int number_of_activities, Instant fetched_at) {
        this.repo_name = repo_name;
        this.number_of_activities = number_of_activities;
        this.fetched_at = fetched_at;
    }
}
