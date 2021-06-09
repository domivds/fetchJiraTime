package be.vdsteen.tools.fetchjiratime.cmdline;

import be.vdsteen.tools.fetchjiratime.action.ListSprints;
import be.vdsteen.tools.fetchjiratime.app.JiraApiCommand;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Getter;

@Parameters(commandDescription = "Get all sprints")
@Getter
public class CommandListSprints implements JiraApiCommand {
  @Parameter(names = "-b", required = true, description = "The board ID")
  private int board;

  @Override
  public void run() {
    ListSprints.run(this);
  }

  @Override
  public void validate() {

  }
}
