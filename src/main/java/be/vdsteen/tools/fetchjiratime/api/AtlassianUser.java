package be.vdsteen.tools.fetchjiratime.api;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@SuppressWarnings("unchecked")
@Data
@Builder
public class AtlassianUser {
  private String accountId;
  private String displayName;

  public static AtlassianUser build(Map<String, Object> jsonUser) {
    return builder()
            .accountId((String) jsonUser.get("accountId"))
            .displayName((String) jsonUser.get("displayName"))
            .build();
  }
}
