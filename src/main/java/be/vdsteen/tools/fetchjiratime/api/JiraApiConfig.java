package be.vdsteen.tools.fetchjiratime.api;

public class JiraApiConfig {
  private String server;
  private String user;
  private String apiKey;

  public String getServer() {
    return server;
  }

  public JiraApiConfig setServer(String server) {
    this.server = server;
    return this;
  }

  public String getUser() {
    return user;
  }

  public JiraApiConfig setUser(String user) {
    this.user = user;
    return this;
  }

  public String getApiKey() {
    return apiKey;
  }

  public JiraApiConfig setApiKey(String apiKey) {
    this.apiKey = apiKey;
    return this;
  }
}
