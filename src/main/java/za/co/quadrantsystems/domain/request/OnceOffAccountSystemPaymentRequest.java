package za.co.quadrantsystems.domain.request;

import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnceOffAccountSystemPaymentRequest {

  private String uetr;

  private String narrative;

  private String debtorPapssId;

  private String debtorAccountIdentification;

  private String debtorBICFI;

  private String creditorBICFI;

  private String creditorPapssId;

  private String creditorAccountIdentification;

  private String debtorFirstName;
  private String debtorLastName;

  private String debtorPhoneNo;

  private String debtorNationalId;

  private String debtorCurrency;

  private String creditorCurrency;

  private String debtorBranchCode;
  private String creditorBranchCode;


  private String creditorFirstName;
  private String creditorLastName;

  private CategoryPurpose categoryPurpose;

  private MethodOfPaymentType methodOfPayment;

  private BigDecimal instructedAmount;
  private BigDecimal senderAmount;
  private String instructedCurrency;

  private String reference;

  private String debtorAddress;

  private String creditorAddress;

  private String instructedDate;

  private Boolean invoice;

  @JsonIgnore
  private List<NotificationDetails> notificationDetails;

  private String debtorCountryCode;

  private String creditorCountryCode;

  private BigDecimal bankFee;

}
