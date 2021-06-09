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
    return builder()
            .id((Integer) jsonSprint.get("id"))
            .name((String) jsonSprint.get("name"))
            .state((String) jsonSprint.get("state"))
            .startDate(JiraModelHelper.toLocalDate((String) jsonSprint.get("startDate")))
            .build();
  }
}
