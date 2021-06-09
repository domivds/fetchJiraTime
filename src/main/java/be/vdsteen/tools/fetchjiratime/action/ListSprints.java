package be.vdsteen.tools.fetchjiratime.action;

import be.vdsteen.tools.fetchjiratime.api.JiraApi;
import be.vdsteen.tools.fetchjiratime.api.Sprint;
import be.vdsteen.tools.fetchjiratime.cmdline.CommandListSprints;

import java.util.List;

public class ListSprints {
  public static void run(CommandListSprints commandConfig) {
    System.out.println("----------------------------------------------------------------------------");
    System.out.println("Running fetch for one sprint:");
    System.out.println("  Board id: " + commandConfig.getBoard());
    System.out.println("  API user: " + JiraApi.getConfig().getUser());
    System.out.println("  JIRA server: " + JiraApi.getConfig().getServer());
    System.out.println("----------------------------------------------------------------------------");


    List<Sprint> sprints = JiraApi.getSprintsForBoard(commandConfig.getBoard());
    for (Sprint sprint : sprints) {
      System.out.println("[" + sprint.getId() + "] " + sprint.getName() + " - " + sprint.getStartDate() + " (" + sprint.getState() + ")");
    }
  }


}
