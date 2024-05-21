package za.co.quadrantsystems.dto.txstatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TxOriginalInfo {
    private String tran_ref;
    private String end_to_end_ref;
    private TxStatus status;
}
