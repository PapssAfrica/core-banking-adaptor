package za.co.quadrantsystems.message.signer.api;

import java.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import za.co.quadrantsystems.message.signer.MessageSigningInterceptor;

@RestController
@Slf4j
@RequestMapping("message-signer")
public class PaymentApi {

  @Autowired
  private SignatureValidator signatureValidator;

  @Autowired
  private MessageSigningInterceptor messageSigningInterceptor;

  @PostMapping("/sign")
  public String sign(@RequestBody final String message,
      @RequestHeader("signature") final String signature,
      @RequestHeader("PAPSS-ID") final String papssId, HttpServletRequest request)
      throws SignatureException {

    // Verify it's papss services asking for the signing
    signatureValidator.verifySignatureForClient(request, "/message-signer/sign");
    log.debug("Signing message {}", message);
    String signedMessage = messageSigningInterceptor.signMessage(message, papssId);

    return signedMessage;

  }

}
