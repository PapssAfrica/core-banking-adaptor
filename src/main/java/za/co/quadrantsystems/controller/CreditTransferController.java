package za.co.quadrantsystems.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import za.co.quadrantsystems.domain.request.PaymentInitiation;
import za.co.quadrantsystems.domain.response.PaymentInitiationResponse;
import za.co.quadrantsystems.message.signer.api.SignatureValidator;
import za.co.quadrantsystems.service.AccountService;

@RestController
@RequestMapping("/core-banking-adaptor")
@RequiredArgsConstructor
public class CreditTransferController {
  @Autowired
  private SignatureValidator signatureValidator;
  @Autowired
  private AccountService accountService;

  @PostMapping("/credit-transfer")
  public PaymentInitiationResponse creditTransfer(
      @RequestBody @Validated final PaymentInitiation paymentInitiation,
      @RequestHeader("PAPSS-ID") final String papssId, HttpServletRequest request)
      throws JsonProcessingException {
    signatureValidator.verifySignatureForClient(request, "/core-banking-adaptor/credit-transfer");

    return accountService.creditPush(paymentInitiation);
  }

}
