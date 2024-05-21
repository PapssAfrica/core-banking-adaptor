package za.co.quadrantsystems.dto.txstatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TxStatus {
    private String type;
    private String reason;
}
