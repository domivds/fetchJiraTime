package be.vdsteen.tools.fetchjiratime.api;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@SuppressWarnings("unchecked")
@Data
@Builder
public class WorkLog {
  private int timeSpentSeconds;
  private AtlassianUser authorUser;
  private LocalDate started;

  public static WorkLog build(Map<String, Object> jsonWorkLog) {
    return builder()
            .started(LocalDate.parse(((String) jsonWorkLog.get("started")).substring(0, 10)))
            .authorUser(AtlassianUser.build((Map<String, Object>) jsonWorkLog.get("author")))
            .timeSpentSeconds((int) jsonWorkLog.get("timeSpentSeconds"))
            .build();
  }
}