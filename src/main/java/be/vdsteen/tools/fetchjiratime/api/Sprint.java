package be.vdsteen.tools.fetchjiratime.api;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@SuppressWarnings("unchecked")
@Data
@Builder
public class Sprint {
  private int id;
  private String name;
  private LocalDate startDate;
  private String state;

  public static Sprint build(Map<String, Object> jsonSprint) {
    String startDate = (String) jsonSprint.get("startDate");
    String state = (String) jsonSprint.get("state");
    SprintBuilder sprintBuilder = builder()
            .id((Integer) jsonSprint.get("id"))
            .name((String) jsonSprint.get("name"))
            .state(state);
    if (null != startDate) {
      sprintBuilder.startDate(JiraModelHelper.toLocalDate(startDate));
    }
    else if (state.equals("future")) {
      sprintBuilder.startDate(LocalDate.MAX);
    }
    else {
      throw new RuntimeException("Sprint " + jsonSprint.get("id") + " has no startDate and is not state 'future'");
    }
    return sprintBuilder.build();

  }
}
