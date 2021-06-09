package be.vdsteen.tools.fetchjiratime.api;

import java.time.LocalDate;

public class JiraModelHelper {

  public static LocalDate toLocalDate(String value) {
    return LocalDate.parse(value.substring(0, 10));
  }
}
