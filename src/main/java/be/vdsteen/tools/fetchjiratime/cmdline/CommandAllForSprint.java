package be.vdsteen.tools.fetchjiratime.cmdline;

import be.vdsteen.tools.fetchjiratime.action.FetchForSprint;
import be.vdsteen.tools.fetchjiratime.app.JiraApiCommand;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Getter;

@Parameters(commandDescription = "Fetch all for sprint")
@Getter
public class CommandAllForSprint implements JiraApiCommand {
  @Parameter(names = "-s", required = true, description = "The sprint ID")
  private String sprint;

  @Parameter(names = "-g", description = "Grouping")
  private Grouping grouping;

  @Parameter(names = "-u", description = "Get info for one user (use 'current' for API user)")
  private String username;

  @Override
  public void run() {
    FetchForSprint.run(this);
  }

  @Override
  public void validate() {

  }

  public enum Grouping {
    perTaskPerDayPerUser, perUserPerTask
  }
}
