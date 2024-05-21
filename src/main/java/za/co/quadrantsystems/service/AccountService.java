package za.co.quadrantsystems.service;

import java.text.SimpleDateFormat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import za.co.quadrantsystems.config.CoreBankingApiConfig;
import za.co.quadrantsystems.config.TokenHolder;
import za.co.quadrantsystems.core.dto.AccountDetailsResponse;
import za.co.quadrantsystems.core.dto.AccountTransferRequest;
import za.co.quadrantsystems.core.dto.AccountTransferResponse;
import za.co.quadrantsystems.core.dto.CancelTransactionResponse;
import za.co.quadrantsystems.domain.request.AccountDetails;
import za.co.quadrantsystems.domain.request.PaymentInitiation;
import za.co.quadrantsystems.domain.response.OnceOffAccountSystemPaymentResponse;
import za.co.quadrantsystems.domain.response.PaymentInitiationResponse;
import za.co.quadrantsystems.dto.nameinquiry.NameInquiryDto;
import za.co.quadrantsystems.dto.nameinquiry.response.NameInquiryResponseDto;
import za.co.quadrantsystems.dto.nameinquiry.response.NameInquiryResponseReceiver;
import za.co.quadrantsystems.dto.nameinquiry.response.NameInquiryResponseSender;
import za.co.quadrantsystems.dto.txstatus.Status;
import za.co.quadrantsystems.dto.txstatus.TxStatusDto;
import za.co.quadrantsystems.dto.txstatusreponse.StatusCode;
import za.co.quadrantsystems.enums.ErrorCodes;

@Service
@Slf4j
public class AccountService {

  SimpleDateFormat dateStringFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z[UTC]'");

  RestTemplate restTemplate;

  ObjectMapper objectMapper;

  CoreBankingApiConfig zbbankingApiConfig;
  TokenHolder tokenHolder;

  public AccountService(CoreBankingApiConfig zbbankingApiConfig, TokenHolder tokenHolder,
      ObjectMapper objectMapper, RestTemplate restTemplate) throws Exception {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
    this.zbbankingApiConfig = zbbankingApiConfig;
    this.tokenHolder = tokenHolder;
    tokenHolder.refreshToken();
  }

  public NameInquiryResponseDto getAccountDetails(final AccountDetails accountDetails)
      throws JsonProcessingException {

    NameInquiryDto nameInquiryDto = accountDetails.getNameInquiryDto();
    NameInquiryResponseDto nameInquiryResponseDto;
    ErrorCodes rejectCode =
        accountDetails.getDirection().equalsIgnoreCase("I") ? ErrorCodes.CODE_1009
            : ErrorCodes.CODE_1008;
    log.debug("Request : {}", accountDetails);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + tokenHolder.getToken().getToken());
    log.info("Sending request to ZBBANKING with data : {}",
        objectMapper.writeValueAsString(nameInquiryDto));
    UriComponents urlBuilder = UriComponentsBuilder
        .fromHttpUrl(zbbankingApiConfig.getCoreBankingUrl() + "/papss-service/api/account-details/"
            + accountDetails.getNameInquiryDto().getReceiver().getAcct_no())
        .build();

    log.info("DownStream request : {}", urlBuilder.toUriString());
    HttpEntity<?> entity = new HttpEntity<>(null, headers);
    ResponseEntity<AccountDetailsResponse> response;
    try {
      response = restTemplate.exchange(urlBuilder.toUriString(), HttpMethod.GET, entity,
          AccountDetailsResponse.class);
    } catch (Exception e) {
      log.error("Error calling core banking with exception : {}", e.getMessage());
      nameInquiryResponseDto = NameInquiryResponseDto.builder()
          .status(Status.builder().code(rejectCode.getValue()).type(StatusCode.RJCT.name())
              .description("Exception calling core banking : " + e.getMessage()).build())
          .end_to_end_ref(nameInquiryDto.getEnd_to_end_ref())
          .sender(NameInquiryResponseSender.builder()
              .inst_id(nameInquiryDto.getSender().getInst_id()).build())
          .tran_ref(nameInquiryDto.getEnd_to_end_ref()).build();
      return nameInquiryResponseDto;
    }


    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

      AccountDetailsResponse accountResponse = response.getBody();

      return NameInquiryResponseDto.builder()
          .status(Status.builder().code(ErrorCodes.UNKNOWN.getValue()).type(StatusCode.ACCP.name())
              .description("accountResponse.getMessages()").build())
          .receiver(NameInquiryResponseReceiver.builder()
              .acct_name("accountResponse.getBody().getAccountName()")
              .acct_no("accountResponse.getBody().getAccountNumber()").build())
          .sender(NameInquiryResponseSender.builder()
              .inst_id(nameInquiryDto.getSender().getInst_id()).build())
          .end_to_end_ref(nameInquiryDto.getEnd_to_end_ref())
          .tran_ref(nameInquiryDto.getEnd_to_end_ref()).build();

    } else if (response.getBody() != null) {
      log.info("Raw Core Banking Response : {}",
          objectMapper.writeValueAsString(response.getBody()));
      nameInquiryResponseDto = NameInquiryResponseDto.builder()
          .status(Status.builder().code(rejectCode.getValue()).type(StatusCode.RJCT.name())
              .description(objectMapper.writeValueAsString(response.getBody())).build())
          .end_to_end_ref(nameInquiryDto.getEnd_to_end_ref())
          .sender(NameInquiryResponseSender.builder()
              .inst_id(nameInquiryDto.getSender().getInst_id()).build())
          .tran_ref(nameInquiryDto.getEnd_to_end_ref()).build();
    } else {
      nameInquiryResponseDto = NameInquiryResponseDto.builder()
          .status(Status.builder().code(rejectCode.getValue()).type(StatusCode.RJCT.name())
              .description("HTTP Error Code : " + response.getStatusCode().value()).build())
          .end_to_end_ref(nameInquiryDto.getEnd_to_end_ref())
          .sender(NameInquiryResponseSender.builder()
              .inst_id(nameInquiryDto.getSender().getInst_id()).build())
          .tran_ref(nameInquiryDto.getEnd_to_end_ref()).build();
    }
    log.debug("Response : {}", nameInquiryResponseDto);
    return nameInquiryResponseDto;
  }

  public PaymentInitiationResponse creditPush(final PaymentInitiation paymentInitiation)
      throws JsonProcessingException {

    AccountTransferRequest request = AccountTransferRequest.builder().build();
    // TODO mapping to account transfer


    // .amount(paymentInitiation.getPaymentInformation().getInstructedAmount().doubleValue())
    // .currency(paymentInitiation.getPaymentInformation().getDebtorCurrency())
    // .senderAccNumber(paymentInitiation.getPaymentInformation().getDebtorAccountIdentification())
    // .receiverAccNumber(
    // paymentInitiation.getPaymentInformation().getCreditorAccountIdentification())
    // .receiverSwiftCode(paymentInitiation.getPaymentInformation().getCreditorBICFI())
    // .narrative(paymentInitiation.getPaymentInformation().getNarrative())
    // .transactionReference(paymentInitiation.getPaymentInformation().getUetr()).build();

    log.info("Request : {}", request);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + tokenHolder.getToken().getToken());
    HttpEntity<AccountTransferRequest> entity =
        new HttpEntity<AccountTransferRequest>(request, headers);
    ResponseEntity<AccountTransferResponse> response;
    PaymentInitiationResponse paymentInitiationResponse;
    try {
      log.info("DownStream call to core banking : {}", objectMapper.writeValueAsString(request));
      response = restTemplate.postForEntity(
          zbbankingApiConfig.getCoreBankingUrl() + "/papss-service/api/transfer  ", entity,
          AccountTransferResponse.class);
    } catch (Exception e) {
      log.error("Exception calling core banking api : {}", e.getMessage());
      paymentInitiationResponse =
          PaymentInitiationResponse.builder().status("CoreBankingFailedResponse")
              .paymentInformation(OnceOffAccountSystemPaymentResponse.builder()
                  .statusReasonCode(ErrorCodes.CODE_1000.getValue())
                  .statusReasonDescription("HTTP Error : " + e.getMessage())
                  .transactionStatus(StatusCode.RJCT)
                  .originalMessageIdentification(
                      paymentInitiation.getGroupHeader().getMessageIdentification())
                  .originalMessageType(paymentInitiation.getGroupHeader().getMessageType())
                  .originalUetr(paymentInitiation.getPaymentInformation().getUetr()).build())
              .direction(paymentInitiation.getDirection())
              .groupHeader(paymentInitiation.getGroupHeader()).build();
      return paymentInitiationResponse;
    }


    if (response.getStatusCode().is2xxSuccessful()
        && StringUtils.isNotBlank("response.getBody().getGatewayReference()")) {

      AccountTransferResponse accountResponse = response.getBody();
      paymentInitiationResponse = PaymentInitiationResponse.builder().status("CoreBankingResponded")
          .paymentInformation(OnceOffAccountSystemPaymentResponse.builder()
              .transactionId("accountResponse.getGatewayReference()")
              .statusReasonCode(ErrorCodes.UNKNOWN.getValue())
              .statusReasonDescription("accountResponse.getNarrative()")
              .transactionStatus(StatusCode.ACCP)
              .originalMessageIdentification(
                  paymentInitiation.getGroupHeader().getMessageIdentification())
              .originalMessageType(paymentInitiation.getGroupHeader().getMessageType())
              .originalUetr(paymentInitiation.getPaymentInformation().getUetr()).build())
          .direction(paymentInitiation.getDirection())
          .groupHeader(paymentInitiation.getGroupHeader()).build();

    } else if (response.getBody() != null) {
      log.info("Raw Core Banking Response : {}",
          objectMapper.writeValueAsString(response.getBody()));
      paymentInitiationResponse = PaymentInitiationResponse.builder().status("CoreBankingResponded")
          .paymentInformation(OnceOffAccountSystemPaymentResponse.builder()
              .statusReasonCode(ErrorCodes.CODE_501.getValue())
              .statusReasonDescription(objectMapper.writeValueAsString(response.getBody()))
              .transactionStatus(StatusCode.RJCT)
              .originalMessageIdentification(
                  paymentInitiation.getGroupHeader().getMessageIdentification())
              .originalMessageType(paymentInitiation.getGroupHeader().getMessageType())
              .originalUetr(paymentInitiation.getPaymentInformation().getUetr()).build())
          .direction(paymentInitiation.getDirection())
          .groupHeader(paymentInitiation.getGroupHeader()).build();

    } else {
      paymentInitiationResponse =
          PaymentInitiationResponse.builder().status("CoreBankingFailedResponse")
              .paymentInformation(OnceOffAccountSystemPaymentResponse.builder()
                  .statusReasonCode(ErrorCodes.CODE_1000.getValue())
                  .statusReasonDescription("HTTP Error Code : " + response.getStatusCode().value())
                  .transactionStatus(StatusCode.RJCT)
                  .originalMessageIdentification(
                      paymentInitiation.getGroupHeader().getMessageIdentification())
                  .originalMessageType(paymentInitiation.getGroupHeader().getMessageType())
                  .originalUetr(paymentInitiation.getPaymentInformation().getUetr()).build())
              .direction(paymentInitiation.getDirection())
              .groupHeader(paymentInitiation.getGroupHeader()).build();

    }
    log.info("Response from core banking : {}",
        objectMapper.writeValueAsString(paymentInitiationResponse));
    return paymentInitiationResponse;
  }

  // --------------------------------------------------------------------------------------------------------------
  public PaymentInitiationResponse recall(
      final PaymentInitiationResponse paymentInitiationResponse) {

    log.info("Revesal Request Received : {}", paymentInitiationResponse);


    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + tokenHolder.getToken().getToken());
    UriComponents urlBuilder = UriComponentsBuilder
        .fromHttpUrl(zbbankingApiConfig.getCoreBankingUrl() + "/papss-service/api/transfer/"
            + paymentInitiationResponse.getPaymentInformation().getTransactionId() + "/cancel")
        .build();
    HttpEntity<?> entity = new HttpEntity<>(null, headers);
    log.info("Reversal Request entity: {} Url: {}", entity, urlBuilder.toUriString());
    ResponseEntity<CancelTransactionResponse> response;
    PaymentInitiationResponse paymentInitiationResponse2;
    try {
      response = restTemplate.exchange(urlBuilder.toUriString(), HttpMethod.PUT, entity,
          CancelTransactionResponse.class);
    } catch (Exception e) {
      log.error("Exception calling core banking api : {}", e.getMessage());
      paymentInitiationResponse2 =
          PaymentInitiationResponse.builder().status("CoreBankingFailedResponse")
              .paymentInformation(OnceOffAccountSystemPaymentResponse.builder()
                  .statusReasonCode(ErrorCodes.CODE_1000.getValue())
                  .statusReasonDescription("HTTP Error Code : " + e.getMessage())
                  .transactionStatus(StatusCode.RJCT)
                  .originalMessageIdentification(
                      paymentInitiationResponse.getGroupHeader().getMessageIdentification())
                  .originalMessageType(paymentInitiationResponse.getGroupHeader().getMessageType())
                  .originalUetr(paymentInitiationResponse.getPaymentInformation().getOriginalUetr())
                  .build())
              .direction(paymentInitiationResponse.getDirection())
              .groupHeader(paymentInitiationResponse.getGroupHeader()).build();
      return paymentInitiationResponse2;
    }

    if (response.getStatusCode().is2xxSuccessful()
        && StringUtils.isNotBlank("response.getBody().getGatewayReference()")) {
      CancelTransactionResponse accountResponse = response.getBody();


      paymentInitiationResponse2 =
          PaymentInitiationResponse.builder().status("CoreBankingResponded")
              .paymentInformation(OnceOffAccountSystemPaymentResponse.builder()
                  .transactionId("accountResponse.getGatewayReference()")
                  .statusReasonDescription("accountResponse.getNarrative()")
                  .transactionStatus(StatusCode.ACCP)
                  .originalMessageIdentification(
                      paymentInitiationResponse.getGroupHeader().getMessageIdentification())
                  .originalMessageType(paymentInitiationResponse.getGroupHeader().getMessageType())
                  .originalUetr(paymentInitiationResponse.getPaymentInformation().getOriginalUetr())
                  .build())
              .direction(paymentInitiationResponse.getDirection())
              .groupHeader(paymentInitiationResponse.getGroupHeader()).build();
    } else {
      paymentInitiationResponse2 =
          PaymentInitiationResponse.builder().status("CoreBankingFailedResponse")
              .paymentInformation(OnceOffAccountSystemPaymentResponse.builder()
                  .statusReasonCode(ErrorCodes.CODE_1000.getValue())// double check this line
                  .statusReasonDescription("HTTP Error Code : " + response.getStatusCode().value())
                  .transactionStatus(StatusCode.RJCT)
                  .originalMessageIdentification(
                      paymentInitiationResponse.getGroupHeader().getMessageIdentification())
                  .originalMessageType(paymentInitiationResponse.getGroupHeader().getMessageType())
                  .originalUetr(paymentInitiationResponse.getPaymentInformation().getOriginalUetr())
                  .build())
              .direction(paymentInitiationResponse.getDirection())
              .groupHeader(paymentInitiationResponse.getGroupHeader()).build();

    }

    return paymentInitiationResponse2;
  }

  public TxStatusDto queryTxStatus(final TxStatusDto txStatusDto) {

    // TODO mapping and api call
    return TxStatusDto.builder().build();
  }
}
