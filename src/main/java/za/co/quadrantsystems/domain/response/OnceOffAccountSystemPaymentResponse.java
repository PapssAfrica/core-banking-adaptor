package za.co.quadrantsystems.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.quadrantsystems.dto.txstatusreponse.StatusCode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnceOffAccountSystemPaymentResponse {

  private String originalMessageType;
  private String originalMessageIdentification;

  private String originalUetr;
  private String transactionId;
  private String originalInstruction;

  private StatusCode transactionStatus;

  private String statusReasonCode;
  private String statusReasonDescription;

}
