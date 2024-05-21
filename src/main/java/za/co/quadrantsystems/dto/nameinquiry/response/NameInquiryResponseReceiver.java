package za.co.quadrantsystems.dto.nameinquiry.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NameInquiryResponseReceiver {
  private String inst_id;
  private String acct_no;
  private String acct_name;
  private String email_address;
}
