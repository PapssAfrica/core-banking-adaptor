package za.co.quadrantsystems.dto.txstatusreponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TxStatus {
    private String type;
    private String reason;
}
