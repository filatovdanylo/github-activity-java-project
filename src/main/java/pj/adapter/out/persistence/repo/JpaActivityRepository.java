package pj.adapter.out.persistence.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pj.adapter.out.persistence.ActivityHistoryEntity;

@Repository
public interface JpaActivityRepository extends JpaRepository<ActivityHistoryEntity, Long> {

    @Query("""
        select count(a)
        from ActivityHistoryEntity a
        where a.repo_name = :repo_name
    """)
    int getNumberOfActivitiesForRepo(String repo_name);

    @Modifying
    @Query("""
    DELETE FROM ActivityHistoryEntity a
        WHERE a.repo_name = :repo_name
    """)
    void deleteByRepoName(String repo_name);

    @Query("""
    SELECT COUNT(A) FROM ActivityHistoryEntity A
        WHERE A.activity_type = :activity_type
    """)
    int getCountByActivity(String activity_type);
}
