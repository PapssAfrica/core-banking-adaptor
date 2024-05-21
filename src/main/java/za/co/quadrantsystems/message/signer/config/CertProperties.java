package za.co.quadrantsystems.message.signer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties
@Configuration
public class CertProperties {
	
	private String privateKeyType;
	private String privateKeyStoreFile;
	private String privateKeyAlias;
	private String privateKeyPassword;
	private String privateExternalUrlBasePath;
	
	private String papssPublicKeyType;
	private String papssPublicStoreFile;
	private String papssPublicAlias;
	private String papssPublicPassword;
}
