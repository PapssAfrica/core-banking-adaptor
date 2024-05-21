package za.co.quadrantsystems.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.quadrantsystems.domain.GroupHeader;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PaymentInitiationResponse {

  private GroupHeader groupHeader;

  private OnceOffAccountSystemPaymentResponse paymentInformation;


  private String status;


  private String direction;
}
