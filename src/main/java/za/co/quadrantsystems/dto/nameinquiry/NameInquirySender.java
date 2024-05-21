package za.co.quadrantsystems.dto.nameinquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class NameInquirySender {

  private String inst_id;

  private String inst_bic;

}
