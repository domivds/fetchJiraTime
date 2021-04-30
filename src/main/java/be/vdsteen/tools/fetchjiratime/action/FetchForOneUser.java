package be.vdsteen.tools.fetchjiratime.action;

import be.vdsteen.tools.fetchjiratime.api.Issue;
import be.vdsteen.tools.fetchjiratime.api.JiraApi;
import be.vdsteen.tools.fetchjiratime.api.WorkLog;
import be.vdsteen.tools.fetchjiratime.cmdline.CommandOneUser;
import be.vdsteen.tools.fetchjiratime.util.DurationHelper;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class FetchForOneUser {

  public static void run(CommandOneUser commandConfig) {
    System.out.println("----------------------------------------------------------------------------");
    System.out.println("Running fetch for one user with params:");
    System.out.println("  Username: " + (null == commandConfig.getUsername() ? "API user" : commandConfig));
    System.out.println("  From date: " + commandConfig.getFromDate());
    System.out.println("  Till date: " + commandConfig.getTillDate());
    System.out.println("  API user: " + JiraApi.getConfig().getUser());
    System.out.println("  JIRA server: " + JiraApi.getConfig().getServer());
    System.out.println("----------------------------------------------------------------------------");

    String userId = JiraApi.findUserId(null == commandConfig.getUsername() ? JiraApi.getConfig().getUser() : commandConfig.getUsername());
    System.out.println("Found userId: " + userId);

    LocalDate runDate = commandConfig.getFromDate();
    while (!runDate.isAfter(commandConfig.getTillDate())) {
      runForDate(runDate, userId);
      runDate = runDate.plusDays(1);
    }
  }


  private static void runForDate(LocalDate date, String userId) {
    System.out.println();
    System.out.println(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + date);

    List<Issue> issues = JiraApi.findIssuesByWorkLog(date, userId);
    issues.forEach(issue -> handleIssue(issue, date, userId));
  }

  private static void handleIssue(Issue issue, LocalDate date, String userId) {
    int totalSeconds = issue.getWorkLogs().stream()
            .filter(workLog -> workLog.getStarted().equals(date))
            .filter(workLog -> workLog.getAuthorUser().getAccountId().equals(userId))
            .map(WorkLog::getTimeSpentSeconds)
            .reduce(0, Integer::sum);
    System.out.println("   " + issue.getKey() + " - " + issue.getTitle() + " - total: " + DurationHelper.formatSeconds(totalSeconds) + " -- " + totalSeconds + "s");
  }

}


