package za.co.quadrantsystems.dto.nameinquiry;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class NameInquiryDto {

  @NotNull(message = "Sender Cannot Be Blank")
  @Valid
  private NameInquirySender sender;

  @NotNull(message = "Receiver Cannot Be Blank")
  @Valid
  private NameInquiryReceiver receiver;

  @NotEmpty(message = "TranRef Cannot Be Blank")
  @Size(min = 4, max = 60, message = "Invalid Character length")
  private String end_to_end_ref;

}
