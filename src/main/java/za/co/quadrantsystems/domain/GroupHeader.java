package za.co.quadrantsystems.domain;

import org.springframework.validation.annotation.Validated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Validated
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupHeader {

  private String papssId;
  private String messageIdentification;

  private String creationDateTime;

  private String initiationSourceName;
  private String operatorIdentification;
  private String operatorType;
  private String responseURI;

  private String messageType;

}
