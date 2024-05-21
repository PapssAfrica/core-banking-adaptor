package za.co.quadrantsystems.message.signer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KeyStoreLoader {

	@Autowired
	ParticipantsConfig config;

	HashMap<String, Pair<RestTemplate, KeyStore>> restTemplateContext = new HashMap<String, Pair<RestTemplate, KeyStore>>();

	protected synchronized KeyStore loadKeystore(final String providerProperty, final String path,
			final char[] password, final String keystoreType) {
		KeyStore result = null;

		Provider provider = Security.getProvider(providerProperty);

		try {
			KeyStore.ProtectionParameter protection = new KeyStore.PasswordProtection(password);
			KeyStore.Builder builder = null;
			if (path == null) {
				builder = KeyStore.Builder.newInstance(keystoreType, provider, protection);
			} else {
				log.info("Loading keystore from: " + path + ". Type: " + keystoreType);
				if (!(new File(path)).exists()) {
					throw new FileNotFoundException("Keystore not found at " + path);
				}
				builder = KeyStore.Builder.newInstance(keystoreType, null, new File(path),
						protection);
			}

			result = builder.getKeyStore();
		} catch (Exception ex) {
			result = null;
			log.error("Could not load the keystore from: " + path, ex);
		}
		return result;
	}

	public RestTemplate getRestTemplate(final String sslKeyAlias) {
		return restTemplateContext.get(sslKeyAlias).getValue0();

	}

	public KeyStore getKeyStore(final String sslKeyAlias) {
		return restTemplateContext.get(sslKeyAlias).getValue1();
	}

	@PostConstruct
	public HashMap<String, Pair<RestTemplate, KeyStore>> init() throws KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, CertificateException, IOException, UnrecoverableKeyException {
		for (Participant participant : config.getParticipantConfigs()) {
			KeyStore participantKeyStore = loadKeystore(participant.getProvider(), participant.getKeyStorePath(),
					participant.getKeyPass().toCharArray(), participant.getKeyStoreType());

			KeyManager[] keyManagers = buildKeyManagers(participantKeyStore, participant.getKeyPass().toCharArray());
			TrustManager[] trustManagers = buildTrustManagers(participantKeyStore);
			SelectableAliasKeyManager sakm = new SelectableAliasKeyManager((X509ExtendedKeyManager) keyManagers[0],
					participant.getSslKeyAlias());

			SSLContext sslContext = new SSLContextBuilder()
					.loadTrustMaterial(new File(participant.getKeyStorePath()),
							participant.getKeyPass().toCharArray())
					.loadKeyMaterial(participantKeyStore, participant.getKeyPass().toCharArray()).build();
			sslContext.init(new KeyManager[] { sakm }, trustManagers, new SecureRandom());

			SSLConnectionSocketFactory sslConFactory = SSLConnectionSocketFactoryBuilder.create()
					.setSslContext(sslContext).setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
					.setTlsVersions(TLS.V_1_2).build();

			final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
					.setSSLSocketFactory(sslConFactory).build();

			CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();

			ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
			restTemplateContext.put(participant.getSslKeyAlias(),
					Pair.with(new RestTemplate(requestFactory), participantKeyStore));
		}
		return restTemplateContext;
	}

	protected KeyManager[] buildKeyManagers(KeyStore store, char[] password)
			throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(store, password);
		return keyManagerFactory.getKeyManagers();
	}

	protected TrustManager[] buildTrustManagers(KeyStore store) throws NoSuchAlgorithmException, KeyStoreException {
		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(store);
		return trustManagerFactory.getTrustManagers();
	}
}
