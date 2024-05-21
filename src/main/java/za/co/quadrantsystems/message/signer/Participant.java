package za.co.quadrantsystems.message.signer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Participant {
    private String inst_bic;
    private String inst_id;
    private String country;
    private String sslKeyAlias;
    private String keyStorePath;
    private String keyPass;
    private String keyStoreType;
    private String provider;
    private String apiKey;
    private String salt;
}
