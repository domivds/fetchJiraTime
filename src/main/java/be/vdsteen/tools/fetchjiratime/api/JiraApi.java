package be.vdsteen.tools.fetchjiratime.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings({"StaticVariableUsedBeforeInitialization", "unchecked"})
public class JiraApi {

  private static JiraApiConfig config;
  private static String authorizationHeader;
  private static String serverBaseUrl;

  public static boolean init() {
    File configFile = new File("fetchJiraTime-config.json");
    if (!configFile.isFile()) {
      System.out.println("Missing config file: " + configFile.getAbsolutePath());
      return false;
    }
    try {
      config = new ObjectMapper().readValue(configFile, JiraApiConfig.class);
      authorizationHeader = "Basic " + Base64.getEncoder().encodeToString((config.getUser() + ":" + config.getApiKey()).getBytes(StandardCharsets.UTF_8));
      serverBaseUrl = config.getServer();
      if (!serverBaseUrl.endsWith("/")) serverBaseUrl += "/";
    }
    catch (IOException e) {
      System.out.println("Failed to load config file: " + configFile.getAbsolutePath());
      e.printStackTrace(System.out);
      return false;
    }


    return true;
  }

  public static JiraApiConfig getConfig() {
    return config;
  }

  public static List<Sprint> getSprintsForBoard(int boardId) {
    Map<String, List<Map<String, Object>>> result = doAction(createSprintsByBoardUrl(boardId), Map.class);

    List<Map<String, Object>> jsonIssues = result.get("values");
    return jsonIssues.stream()
            .map(Sprint::build)
            .sorted(Comparator.comparing(Sprint::getStartDate))
            .collect(Collectors.toList());
  }

  public static List<Issue> findIssuesForSprint(String sprintId) {
    return findIssues(createSearchBySprint(sprintId));
  }

  public static String findUserId(String userId) {
    //https://bkdev.atlassian.net/rest/api/3/user/search?query=thomas.grivegnee@cogni.zone
    List<Map<String, Object>> users = doAction(createFindUserUrl(userId), List.class);
    if (users.size() != 1) throw new RuntimeException("Not just 1 user for " + userId);

    String accountId = (String) users.get(0).get("accountId");
    if (null == accountId || accountId.isBlank()) throw new RuntimeException("No accountId for " + userId);
    return accountId;
  }

  public static List<WorkLog> getWorklogsForIssueById(String issueId) {
    Map<String, Object> jsonWorklogsWrapper = doAction(createWorklogsForIssueByIdUrl(issueId), Map.class);
    List<Map<String, Object>> jsonWorklogs = (List<Map<String, Object>>) jsonWorklogsWrapper.get("worklogs");
    int total = (int) jsonWorklogsWrapper.get("total");
    if (total != jsonWorklogs.size()) {
      throw new RuntimeException("Get worklogs for issue didn't return expected count: " + createWorklogsForIssueByIdUrl(issueId));
    }
    return jsonWorklogs.stream()
            .map(WorkLog::build)
            .collect(Collectors.toList());
  }

  public static <T> T doAction(String url, Class<T> expectedResultType) {
    if (null == config) throw new RuntimeException("Not initialized");

    HttpURLConnection urlConnection = null;
    try {
      URL urlObject = new URL(url);
      urlConnection = (HttpURLConnection) urlObject.openConnection();

      urlConnection.addRequestProperty("Authorization", authorizationHeader);
      try (InputStream inputStream = urlConnection.getInputStream()) {
        return new ObjectMapper().readValue(inputStream, expectedResultType);
      }
      catch (IOException e) {
        System.out.println("Error message: " + fetchErrorMessage(urlConnection));
        throw new RuntimeException("Failed to call JIRA API", e);
      }
    }
    catch (IOException e) {
      //System.out.println("Error message: " + fetchErrorMessage(urlConnection));
      throw new RuntimeException("Failed to call JIRA API", e);
    }
  }

  private static String fetchErrorMessage(HttpURLConnection urlConnection) {
    if (null == urlConnection) return "URL error?";
    try {
      return IOUtils.toString(urlConnection.getErrorStream(), StandardCharsets.UTF_8);
    }
    catch (IOException e) {
      return "Cannot fetch error message: " + e.getMessage();
    }
  }

  public static List<Issue> findIssuesByWorkLog(LocalDate date, String userId) {
    return findIssues(createSearchByWorklogForUser(date, userId));
  }

  public static Optional<Sprint> findSprintById(String sprintId) {
    try {
      Map<String, Object> jsonSprint = doAction(createSprintSelfUrl(sprintId), Map.class);
      return Optional.of(Sprint.build(jsonSprint));
    }
    catch (Exception exception) {
      return Optional.empty();
    }
  }

  private static List<Issue> findIssues(String url) {
    Map<String, List<Map<String, Object>>> result = doAction(url, Map.class);

    List<Map<String, Object>> jsonIssues = result.get("issues");
    return jsonIssues.stream()
            .map(Issue::build)
            .collect(Collectors.toList());
  }

  private static String createSearchByWorklogForUser(LocalDate date, String userId) {
    String dateString = date.toString();
    return serverBaseUrl + "rest/api/2/search?fields=summary,worklog&maxResults=1000" +
           "&jql=worklogDate=" + dateString + "%20and%20timespent%3E0" +
           "%20and%20worklogAuthor=\"" + URLEncoder.encode(userId, StandardCharsets.UTF_8) + "\"";
  }

  private static String createSearchBySprint(String spring) {
    return serverBaseUrl + "rest/api/2/search?fields=summary,worklog&maxResults=1000&jql=sprint=" + spring;
  }

  private static String createFindUserUrl(String email) {
    //https://bkdev.atlassian.net/rest/api/3/user/search?query=thomas.grivegnee@cogni.zone
    return serverBaseUrl + "rest/api/3/user/search?query=" + email;
  }

  private static String createWorklogsForIssueByIdUrl(String issueId) {
    return serverBaseUrl + "rest/api/3/issue/" + issueId + "/worklog";
  }

  private static String createSprintSelfUrl(String sprintId) {
    return serverBaseUrl + "rest/agile/1.0/sprint/" + sprintId;
  }

  private static String createSprintsByBoardUrl(int boardId) {
    return serverBaseUrl + "rest/agile/1.0/board/" + boardId + "/sprint";
  }
}
