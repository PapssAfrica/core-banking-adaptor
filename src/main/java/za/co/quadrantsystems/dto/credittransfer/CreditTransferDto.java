package za.co.quadrantsystems.dto.credittransfer;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditTransferDto {

  // @NotNull(message = "tran_ref cannot be blank")
  // private String original_ref;

  @NotNull(message = "end_to_end_ref cannot be blank")
  private String end_to_end_ref;

  @NotNull(message = "tran_date_time cannot be blank")
  private String tran_date_time;

  // @NotNull(message = "xchg_rate cannot be blank")
  // private String xchg_rate;

  @NotNull(message = "narration cannot be blank")
  // @Pattern(regexp= RegularExpressions., message="narration Can Only Be Alphanumeric")
  @Size(max = 140)
  private String narration;


  @NotNull(message = "is_invoice cannot be blank")
  @JsonProperty("is_invoice")
  private Boolean isInvoice;

  @NotNull(message = "ctgy_purp cannot be blank")
  private String ctgy_purp;

  @NotNull(message = "lc_instrmt_type cannot be blank")
  private String lc_instrmt_type;

  @DecimalMin(value = "0.00", inclusive = false)
  @Digits(integer = 100, fraction = 2)
  private BigDecimal amount;

  @NotNull(message = "Sender Cannot Be Blank")
  @Valid
  private SenderDetails sender;

  @NotNull(message = "Receiver Cannot Be Blank")
  @Valid
  private ReceiverDetails receiver;

}
