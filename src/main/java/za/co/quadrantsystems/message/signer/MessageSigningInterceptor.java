package za.co.quadrantsystems.message.signer;

import java.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageSigningInterceptor {

  @Autowired
  PapssSignature papssSignature;

  @Autowired
  ParticipantsConfig config;


  public String signMessage(final String messageBytes, final String papssId)
      throws SignatureException {
    Participant participant = config.getParticipantConfig(papssId);
    log.info("Sign request received from Papss with data : {}", messageBytes);
    return papssSignature.generateSignature(messageBytes, participant.getSslKeyAlias(),
        participant.getKeyPass());
  }

}
