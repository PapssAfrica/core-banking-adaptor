package za.co.quadrantsystems.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import za.co.quadrantsystems.dto.auth.Token;
import za.co.quadrantsystems.dto.auth.AuthRequest;

@Service
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class TokenHolder {

  public Token token;

  @Autowired
  public RestTemplate restTemplate;

  @Autowired
  private CoreBankingApiConfig zBBANKINGApiConfig;

  public void refreshToken() throws Exception {
    if (zBBANKINGApiConfig.isIntegrated()) {
      log.info("About to Login to NBS...");

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      AuthRequest zbBankAuthRequest =
          AuthRequest.builder().username(zBBANKINGApiConfig.getUsername())
              .password(zBBANKINGApiConfig.getPassword()).build();
      HttpEntity<AuthRequest> entity =
          new HttpEntity<AuthRequest>(zbBankAuthRequest, headers);
      ResponseEntity<Token> response = restTemplate.postForEntity(
          zBBANKINGApiConfig.getAuthUrl() + "/papss-service/api/v1/users/authenticate", entity,
          Token.class);
      if (response.getStatusCode().is2xxSuccessful()) {
        token = response.getBody();
        log.info("Successfully Logged in to ZBBANK. Response ===> {}",
            new ObjectMapper().writeValueAsString(response.getBody()));
      } else {

        log.error("Error logging into ZBBANK Bank api with http Error : ",
            response.getStatusCode().value());
        throw new Exception("Unable to get Authentication token with response HTTPCode: "
            + response.getStatusCode().value());
      }
    }
  }
}
