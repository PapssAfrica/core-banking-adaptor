package za.co.quadrantsystems.dto.txstatus;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class TxStatusDto {

    private String tran_ref;
	private String inst_id;
    private String tran_date_time;
    private TxOriginalInfo orgnl_txn_info;
    private Status status;

}
