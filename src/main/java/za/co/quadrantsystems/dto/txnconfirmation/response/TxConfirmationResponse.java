package za.co.quadrantsystems.dto.txnconfirmation.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.quadrantsystems.dto.txstatus.Status;

@Data
@NoArgsConstructor
public class TxConfirmationResponse {
  private String tran_ref;
  private String tran_date_time;
  private String inst_id;
  private Status status;

}
