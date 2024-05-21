package za.co.quadrantsystems.dto.credittransfer;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;



@Data
@Builder
public class ReceiverDetails {

  @NotEmpty(message = "inst_id Field is Required")
  @Size(min = 4, max = 15, message = "Invalid Character length")
  @JsonProperty
  private String inst_id;

  @NotEmpty(message = "inst_bic Field is Required")
  @Size(min = 4, max = 15, message = "Invalid Character length")
  private String inst_bic;

  @NotEmpty(message = "ctry Field is Required")
  @Size(min = 2, max = 60, message = "Invalid Character length")
  private String ctry;

  @NotEmpty(message = "acct_no Field is Required")
  @Size(min = 2, max = 60, message = "Invalid Character length")
  private String acct_no;

  @NotEmpty(message = "acct_name Field is Required")
  @Size(min = 2, max = 60, message = "Invalid Character length")
  private String acct_name;

  @NotEmpty(message = "ccy Field is Required")
  @Size(min = 2, max = 60, message = "Invalid Character length")
  private String ccy;

  // @DecimalMin(value = "0.00", inclusive = false)
  // @Digits(integer=100, fraction=2)
  // private BigDecimal amount;

  private String address;
}
