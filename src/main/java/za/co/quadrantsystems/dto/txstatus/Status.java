package za.co.quadrantsystems.dto.txstatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Status {
  private String type;
  private String code;
  private String description;
}
