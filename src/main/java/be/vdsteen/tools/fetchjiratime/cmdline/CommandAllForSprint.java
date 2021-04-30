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

  @Override
  public void run() {
    FetchForSprint.run(this);
  }

  @Override
  public void validate() {

  }
}
