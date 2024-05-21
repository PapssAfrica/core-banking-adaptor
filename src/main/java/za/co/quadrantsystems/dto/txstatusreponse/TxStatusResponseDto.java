package za.co.quadrantsystems.dto.txstatusreponse;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import za.co.quadrantsystems.dto.txstatus.Status;
import za.co.quadrantsystems.dto.txstatus.TxOriginalInfo;

@Data
@Builder
@ToString
public class TxStatusResponseDto {

  private String tran_ref;
  private String tran_date_time;
  private TxOriginalInfo orgnl_txn_info;
  private Status status;

}
