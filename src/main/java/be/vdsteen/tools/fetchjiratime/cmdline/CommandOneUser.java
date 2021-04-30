package be.vdsteen.tools.fetchjiratime.cmdline;

import be.vdsteen.tools.fetchjiratime.app.JiraApiCommand;
import be.vdsteen.tools.fetchjiratime.action.FetchForOneUser;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Getter;

import java.time.LocalDate;

@Parameters(commandDescription = "Fetch data for one user")
@Getter
public class CommandOneUser implements JiraApiCommand {
  @Parameter(names = {"-u", "--username"}, description = "UserID (defaults to connecting user)")
  private String username;

  @Parameter(names = {"-f", "--from"}, required = true, description = "From date (format 2021-04-24)", converter = LocalDateConvertor.class)
  private LocalDate fromDate;

  @Parameter(names = {"-t", "--till"}, description = "Till date inclusive(format 2021-04-24 - defaults to from date)", converter = LocalDateConvertor.class)
  private LocalDate tillDate;

  public LocalDate getTillDate() {
    return null == tillDate ? fromDate : tillDate;
  }

  @Override
  public void run() {
    FetchForOneUser.run(this);
  }

  @Override
  public void validate() {
    if(getTillDate().isBefore(getFromDate())) throw new RuntimeException("Till date cannot be before from date");
  }
}
