package be.vdsteen.tools.fetchjiratime.action;

import be.vdsteen.tools.fetchjiratime.api.AtlassianUser;
import be.vdsteen.tools.fetchjiratime.api.Issue;
import be.vdsteen.tools.fetchjiratime.api.JiraApi;
import be.vdsteen.tools.fetchjiratime.api.Sprint;
import be.vdsteen.tools.fetchjiratime.api.WorkLog;
import be.vdsteen.tools.fetchjiratime.cmdline.CommandAllForSprint;
import be.vdsteen.tools.fetchjiratime.util.DurationHelper;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class FetchForSprint {

  public static void run(CommandAllForSprint commandConfig) {
    System.out.println("----------------------------------------------------------------------------");
    System.out.println("Running fetch for one sprint:");
    System.out.println("  Sprint id: " + commandConfig.getSprint());
    System.out.println("  API user: " + JiraApi.getConfig().getUser());
    System.out.println("  JIRA server: " + JiraApi.getConfig().getServer());
    System.out.println("----------------------------------------------------------------------------");

    Optional<Sprint> sprint = JiraApi.findSprintById(commandConfig.getSprint());
    if (sprint.isEmpty()) {
      System.out.println("Sprint with id " + commandConfig.getSprint() + " not found");
      return;
    }

    String userId = null;
    if (null != commandConfig.getUsername()) {
      userId = JiraApi.findUserId(commandConfig.getUsername().equals("current") ? JiraApi.getConfig().getUser() : commandConfig.getUsername());
      System.out.println("Requested user: " + commandConfig.getUsername());
      System.out.println("Found userId: " + userId);
    }

    List<Issue> issuesForSprint = JiraApi.findIssuesForSprint(commandConfig.getSprint());
    Map<Issue, Map<AtlassianUser, Integer>> timePerIssuePerUser = new HashMap<>();
    Map<Issue, Map<AtlassianUser, SortedMap<LocalDate, Integer>>> timePerIssuePerUserPerDay = new HashMap<>();
    Map<AtlassianUser, Integer> timePerUser = new HashMap<>();
    for (Issue issue : issuesForSprint) {
      for (WorkLog workLog : issue.getWorkLogs()) {
        int seconds = workLog.getTimeSpentSeconds();

        if (seconds == 0) continue;

        AtlassianUser authorUser = workLog.getAuthorUser();
        if (null != userId && !authorUser.getAccountId().equals(userId)) continue;

        Map<AtlassianUser, Integer> timePerUserForIssue = timePerIssuePerUser.computeIfAbsent(issue, key -> new HashMap<>());
        timePerUserForIssue.put(authorUser, seconds + timePerUserForIssue.getOrDefault(authorUser, 0));
        timePerUser.put(authorUser, seconds + timePerUser.getOrDefault(authorUser, 0));

        Map<AtlassianUser, SortedMap<LocalDate, Integer>> timePerDayPerUser = timePerIssuePerUserPerDay.computeIfAbsent(issue, key -> new HashMap<>());
        SortedMap<LocalDate, Integer> timerPerDay = timePerDayPerUser.computeIfAbsent(authorUser, key -> new TreeMap<>());
        timerPerDay.put(workLog.getStarted(), seconds + timerPerDay.getOrDefault(workLog.getStarted(), 0));
      }
    }
    System.out.println();
    System.out.println("Total time per issue for sprint (" + sprint.get().getName() + "):");

    if (commandConfig.getGrouping() == CommandAllForSprint.Grouping.perTaskPerDayPerUser) outputPerTaskPerDayPerUser(timePerIssuePerUserPerDay, null == userId);
    else if (commandConfig.getGrouping() == CommandAllForSprint.Grouping.perUserPerTask) outputPerUserPerTask(timePerIssuePerUser);
    else outputGlobalTimePerUser(timePerIssuePerUser, null == userId);

    System.out.println();
    System.out.println("Total time per user for sprint (" + sprint.get().getName() + "):");
    timePerUser.forEach((atlassianUser, totalSeconds) -> {
      System.out.println("  " + atlassianUser.getDisplayName() + " : " + DurationHelper.formatSeconds(totalSeconds) + " -- " + totalSeconds + "s");
    });
  }

  private static void outputPerTaskPerDayPerUser(Map<Issue, Map<AtlassianUser, SortedMap<LocalDate, Integer>>> timePerIssuePerUserPerDay, boolean multiUser) {
    timePerIssuePerUserPerDay.forEach((issue, timePerUserPerDayForIssue) -> {
      System.out.println("  " + issue.getKey() + " " + issue.getTitle());
      AtomicInteger totalForIssue = new AtomicInteger(0);
      timePerUserPerDayForIssue.forEach((atlassianUser, secondsPerDay) -> {
        AtomicInteger totalForUser = new AtomicInteger(0);
        if (multiUser) System.out.println("      " + atlassianUser.getDisplayName() + ": ");
        secondsPerDay.forEach((day, totalSeconds) -> {
          System.out.println("        " + day + ": " + DurationHelper.formatSeconds(totalSeconds) + " -- " + totalSeconds + "s");
          totalForIssue.addAndGet(totalSeconds);
          totalForUser.addAndGet(totalSeconds);
        });
        if (multiUser) System.out.println("        Total: " + DurationHelper.formatSeconds(totalForUser.get()) + " -- " + totalForUser.get() + "s");
      });
      if (multiUser) System.out.println("      Total: " + DurationHelper.formatSeconds(totalForIssue.get()) + " -- " + totalForIssue.get() + "s");
    });
  }

  private static void outputPerUserPerTask(Map<Issue, Map<AtlassianUser, Integer>> timePerIssuePerUser) {
    Map<AtlassianUser, Map<Issue, Integer>> inversed = new HashMap<>();

    timePerIssuePerUser.forEach((issue, atlassianUserIntegerMap) -> {
      for (Map.Entry<AtlassianUser, Integer> entry : atlassianUserIntegerMap.entrySet()) {
        Map<Issue, Integer> issuesForThisUser = inversed.computeIfAbsent(entry.getKey(), key -> new HashMap<>());
        issuesForThisUser.put(issue, entry.getValue() + issuesForThisUser.getOrDefault(issue, 0));
      }
    });

    for (Map.Entry<AtlassianUser, Map<Issue, Integer>> entry : inversed.entrySet()) {
      System.out.println("  " + entry.getKey().getDisplayName());
      int total = 0;
      for (Map.Entry<Issue, Integer> timePerIssue : entry.getValue().entrySet()) {
        total += timePerIssue.getValue();
        System.out.println("    " + timePerIssue.getKey().getKey() + " " + timePerIssue.getKey().getTitle() + ": " + DurationHelper.formatSeconds(timePerIssue.getValue()));
      }
      System.out.println("    TOTAL: " + DurationHelper.formatSeconds(total));
    }
  }

  private static void outputGlobalTimePerUser(Map<Issue, Map<AtlassianUser, Integer>> timePerIssuePerUser, boolean multiUser) {
    timePerIssuePerUser.forEach((issue, timePerUserForIssue) -> {
      System.out.println("  " + issue.getKey() + " " + issue.getTitle());
      AtomicInteger totalForIssue = new AtomicInteger(0);
      timePerUserForIssue.forEach((atlassianUser, totalSeconds) -> {
        if (multiUser) System.out.println("      " + atlassianUser.getDisplayName() + ": " + DurationHelper.formatSeconds(totalSeconds) + " -- " + totalSeconds + "s");
        else System.out.println("      " + DurationHelper.formatSeconds(totalSeconds) + " -- " + totalSeconds + "s");
        totalForIssue.addAndGet(totalSeconds);
      });
      if (multiUser) System.out.println("      Total: " + DurationHelper.formatSeconds(totalForIssue.get()) + " -- " + totalForIssue.get() + "s");
    });
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


