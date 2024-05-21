package za.co.quadrantsystems.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import za.co.quadrantsystems.domain.request.AccountDetails;
import za.co.quadrantsystems.dto.nameinquiry.response.NameInquiryResponseDto;
import za.co.quadrantsystems.dto.txstatusreponse.StatusCode;
import za.co.quadrantsystems.message.signer.api.SignatureValidator;
import za.co.quadrantsystems.service.AccountService;

@RestController
@RequestMapping("/core-banking-adaptor")
public class AccountEnquiryController {
  @Autowired
  private SignatureValidator signatureValidator;
  @Autowired
  private AccountService accountService;

  @PostMapping("/onus/name-enquiry")
  public NameInquiryResponseDto accountEnquiry(@RequestBody final AccountDetails accountDetails,
      @RequestHeader("PAPSS-ID") final String papssId, HttpServletRequest request)
      throws JsonProcessingException {
    signatureValidator.verifySignatureForClient(request, "/core-banking-adaptor/onus/name-enquiry");

    if (!StringUtils.hasText(accountDetails.getNameInquiryDto().getReceiver().getCcy())) {
      accountDetails.getNameInquiryDto().getReceiver().setCcy("USD");
      NameInquiryResponseDto response;
      response = accountService.getAccountDetails(accountDetails);
      if (response.getStatus().getType().equalsIgnoreCase(StatusCode.RJCT.name())) {
        accountDetails.getNameInquiryDto().getReceiver().setCcy("ZIG");
        response = accountService.getAccountDetails(accountDetails);
      }
      return response;
    } else {
      return accountService.getAccountDetails(accountDetails);
    }
  }

}
