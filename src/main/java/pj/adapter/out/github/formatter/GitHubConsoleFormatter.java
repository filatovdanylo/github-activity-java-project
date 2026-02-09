package pj.adapter.out.github.formatter;

import org.springframework.stereotype.Component;
import pj.domain.model.Activity;
import pj.domain.model.ActivityType;
import java.util.List;

@Component
public class GitHubConsoleFormatter {

    public String format(List<Activity> activities) {
        if (activities == null || activities.isEmpty()) {
            return "No recent activity found";
        }

        StringBuilder output = new StringBuilder();
        output.append("Recent activity:\n");

        for (Activity activity : activities) {
            String formatted = formatActivity(activity);
            output.append("- ").append(formatted).append("\n");
        }

        return output.toString();
    }

    private String formatActivity(Activity activity) {
        String repo = activity.repositoryName();

        return switch (activity.type()) {
            case PUSH -> "Pushed to " + repo;
            case CREATE -> "Created in " + repo;
            case STARRED -> "Starred " + repo;
            case FORK -> "Forked " + repo;
            case ISSUE_OPENED -> "Opened an issue in " + repo;
            case PULL_REQUEST -> "Opened a pull request in " + repo;
            case OTHER -> "Activity in " + repo;
        };
    }
}