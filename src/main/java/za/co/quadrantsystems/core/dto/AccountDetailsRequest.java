package za.co.quadrantsystems.core.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AccountDetailsRequest {
  private String accountNumber;
  // TODO place fields retured by corebanking here
}
