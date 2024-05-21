package za.co.quadrantsystems.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.quadrantsystems.domain.GroupHeader;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PaymentInitiation {

  private GroupHeader groupHeader;

  private OnceOffAccountSystemPaymentRequest paymentInformation;

  private String status;

  private String direction;
}
