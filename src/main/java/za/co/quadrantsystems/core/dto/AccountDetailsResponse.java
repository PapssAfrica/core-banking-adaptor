package za.co.quadrantsystems.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsResponse {

  private boolean success;
  // TODO place fields retured by corebanking here
}
