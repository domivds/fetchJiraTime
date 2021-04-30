package be.vdsteen.tools.fetchjiratime.api;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@SuppressWarnings("unchecked")
@Data
@Builder
public class Sprint {
  private String name;

  public static Sprint build(Map<String, Object> jsonSprint) {
    return builder()
            .name((String) jsonSprint.get("name"))
            .build();
  }
}
