package be.vdsteen.tools.fetchjiratime.app;

import be.vdsteen.tools.fetchjiratime.api.JiraApi;
import be.vdsteen.tools.fetchjiratime.cmdline.JCommanderBuilder;
import com.beust.jcommander.JCommander;

import java.util.List;

public class FetchJiraTime {

  public static void main(String[] args) throws Exception {
    if (!JiraApi.init()) {
      System.out.println("Failed to initialize API setup");
      return;
    }

    JCommander jCommander = JCommanderBuilder.build();
    JiraApiCommand jiraApiCommand;
    try {
      jCommander.parse(args);
      String command = jCommander.getParsedCommand();
      if (null == command) throw new RuntimeException("No command found");
      List<Object> commandObjects = jCommander.getCommands().get(command).getObjects();
      if (commandObjects.size() != 1) throw new RuntimeException("Not exactly 1 command object found");
      Object commandObject = commandObjects.get(0);
      if (!(commandObject instanceof JiraApiCommand)) throw new RuntimeException("Found command object is not a JiraApiCommand");
      jiraApiCommand = (JiraApiCommand) commandObject;
      jiraApiCommand.validate();
    }
    catch (Exception exception) {
      System.out.println(exception.getMessage());
      jCommander.usage();
      return;
    }
    jiraApiCommand.run();
    System.out.println();
    System.out.println("----------------------------------------------------------------------------");
  }


}
