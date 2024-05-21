package za.co.quadrantsystems.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountTransferResponse {
  private boolean success;
  // TODO place fields retured by corebanking here

}
