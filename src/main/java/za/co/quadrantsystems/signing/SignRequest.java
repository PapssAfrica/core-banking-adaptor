package za.co.quadrantsystems.signing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class SignRequest {

	private String xml;
	private String papssId;

}
