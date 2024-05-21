package za.co.quadrantsystems.core.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetTransactionResponse {
  private boolean success;
  // TODO place fields retured by corebanking here

}
