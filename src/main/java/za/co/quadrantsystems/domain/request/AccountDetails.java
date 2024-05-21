package za.co.quadrantsystems.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.quadrantsystems.domain.GroupHeader;
import za.co.quadrantsystems.dto.nameinquiry.NameInquiryDto;
import za.co.quadrantsystems.dto.nameinquiry.response.NameInquiryResponseDto;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class AccountDetails {
  private GroupHeader groupHeader;

  private NameInquiryDto nameInquiryDto;

  private NameInquiryResponseDto nameInquiryResponseDto;

  private String status;

  private String direction;
}
