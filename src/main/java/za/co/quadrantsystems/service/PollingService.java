package za.co.quadrantsystems.service;

import java.net.InetAddress;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@EnableScheduling
public class PollingService {

  @Scheduled(fixedRate = 3000)
  public void polling() throws Exception {

    InetAddress address = InetAddress.getByName("10.109.10.152");

    // Use ping method to check reachability
    if (address.isReachable(5000)) { // Adjust timeout value (milliseconds) if needed
      log.trace("Host 10.109.10.152 is reachable!");
    } else {
      log.trace("Host 10.109.10.152 is unreachable!");
    }

  }

}
