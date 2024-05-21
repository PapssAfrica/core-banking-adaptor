package za.co.quadrantsystems.dto.nameinquiry.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.quadrantsystems.dto.txstatus.Status;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NameInquiryResponseDto {
  private String tran_ref;
  private String end_to_end_ref;
  private NameInquiryResponseSender sender;
  private NameInquiryResponseReceiver receiver;

  private Status status;

}
