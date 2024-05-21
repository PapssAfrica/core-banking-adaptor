package za.co.quadrantsystems.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
@Configuration
@Slf4j
public class ApplicationConfig {
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
