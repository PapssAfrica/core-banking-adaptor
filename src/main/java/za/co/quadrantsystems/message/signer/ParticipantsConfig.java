package za.co.quadrantsystems.message.signer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import lombok.Data;
import za.co.quadrantsystems.outbound.exception.GenericExceptionHandler;

@Data
@Configuration
@Component
@ConfigurationProperties(prefix = "papss")
public class ParticipantsConfig {
  private List<Participant> participants = new ArrayList<>();

  ParticipantsConfig() {

  }

  private Participant participantConfigFor(final String papssId) {
    return participants.stream().filter(participant -> participant.getInst_id().equals(papssId))
        .findFirst().orElse(null);
  }

  public Participant getParticipantConfig(final String papssId) throws GenericExceptionHandler {
    final Participant participantFor = this.participantConfigFor(papssId);
    if (Objects.isNull(participantFor)) {
      throw new GenericExceptionHandler("Invalid PAPSS ID");
    }
    return participantFor;
  }

  public List<Participant> getParticipantConfigs() {
    return participants.stream()
        .filter(participant -> !participant.getInst_id().contains("${PAPSS_"))
        .collect(Collectors.toList());
  }

  public void validateParticipantByApiKey(final String authorization,
      final Participant participant) {
    if (!authorization.equals(participant.getApiKey())) {
      throw new GenericExceptionHandler("Invalid API Key Authentication");
    }
  }
}
