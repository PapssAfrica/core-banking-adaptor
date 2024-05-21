package za.co.quadrantsystems.dto.nameinquiry.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NameInquiryResponseSender {
  private String inst_id;
}
