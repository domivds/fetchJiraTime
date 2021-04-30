package be.vdsteen.tools.fetchjiratime.api;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@Data
@Builder
public class Issue {
  private String title;
  private String key;
  private List<WorkLog> workLogs;

  public static Issue build(Map<String, Object> jsonIssue) {
    Map<String, Object> jsonIssueFields = (Map<String, Object>) jsonIssue.get("fields");
    Map<String, Object> jsonIssueWorkLogsWrapper = (Map<String, Object>) jsonIssueFields.get("worklog");
    List<Map<String, Object>> jsonWorkLogs = (List<Map<String, Object>>) jsonIssueWorkLogsWrapper.get("worklogs");

    List<WorkLog> workLogs;
    int total = (int) jsonIssueWorkLogsWrapper.get("total");
    if (total == jsonWorkLogs.size()) {
      workLogs = jsonWorkLogs.stream().map(WorkLog::build).collect(Collectors.toList());
    }
    else {
      String issueId = (String) jsonIssue.get("id");
      workLogs = JiraApi.getWorklogsForIssueById(issueId);
    }


    return builder()
            .key((String) jsonIssue.get("key"))
            .title((String) jsonIssueFields.get("summary"))
            .workLogs(workLogs)
            .build();
  }
}