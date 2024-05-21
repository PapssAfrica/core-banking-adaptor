package za.co.quadrantsystems.dto.recon;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReconDto {
    private String tran_ref;
    private String tran_date_time;
    private String inst_id;
    private String recon_xml_payload;
    private String recon_json_payload;
}
