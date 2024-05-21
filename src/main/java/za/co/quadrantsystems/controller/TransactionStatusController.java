package za.co.quadrantsystems.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import za.co.quadrantsystems.dto.txstatus.TxStatusDto;
import za.co.quadrantsystems.message.signer.api.SignatureValidator;
import za.co.quadrantsystems.service.AccountService;

@RestController
@RequestMapping("/core-banking-adaptor")
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatusController {
  @Autowired
  private SignatureValidator signatureValidator;
  private AccountService accountService;

  @PostMapping("/transactions/status")
  public TxStatusDto creditorAccountEnquiry(@RequestBody @Validated final TxStatusDto txStatusDto,
      @RequestHeader("PAPSS-ID") final String papssId, HttpServletRequest request)
      throws Exception {
    signatureValidator.verifySignatureForClient(request,
        "/core-banking-adaptor/transactions/status");
    return accountService.queryTxStatus(txStatusDto);
  }

}
