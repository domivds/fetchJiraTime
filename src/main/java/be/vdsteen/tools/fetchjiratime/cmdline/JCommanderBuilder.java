package be.vdsteen.tools.fetchjiratime.cmdline;

import com.beust.jcommander.JCommander;

public class JCommanderBuilder {

  public static JCommander build() {

    return JCommander.newBuilder()
            .addCommand("allForSprint", new CommandAllForSprint())
            .addCommand("oneUser", new CommandOneUser())
            .build();
  }
}
