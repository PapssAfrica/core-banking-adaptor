package za.co.quadrantsystems.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@ConfigurationProperties(prefix = "core.banking.api")
@Data
@Configuration
public class CoreBankingApiConfig {

  private String authUrl;
  private String coreBankingUrl;
  private String username;
  private String password;
  private boolean integrated;
  private String bankTransactionCode;
}
