package pj.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pj.adapter.out.persistence.ActivityRepositoryAdapter;
import pj.app.port.in.LoadUserActivityUseCase;
import pj.app.port.out.FetchGithubEventsPort;
import pj.domain.model.Activity;

import java.util.List;

@Service
public class GitHubActivityService implements LoadUserActivityUseCase {

    private final FetchGithubEventsPort gitHubApiClient;
    private final ActivityRepositoryAdapter activityRepositoryAdapter;

    @Autowired
    public GitHubActivityService(FetchGithubEventsPort gitHubApiClient, ActivityRepositoryAdapter activityRepositoryAdapter) {
        this.gitHubApiClient = gitHubApiClient;
        this.activityRepositoryAdapter = activityRepositoryAdapter;
    }

    @Cacheable(value = "activities", key = "#username")
    public List<Activity> getUserActivity(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        var activity = gitHubApiClient.fetchUserEvents(username);

        if (activity != null && !activity.isEmpty())
            activityRepositoryAdapter.saveAll(activity);

        return activity;
    }

}
