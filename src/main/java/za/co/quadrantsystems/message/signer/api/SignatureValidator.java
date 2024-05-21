package za.co.quadrantsystems.message.signer.api;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import za.co.quadrantsystems.message.signer.config.CertProperties;

@Service
@Slf4j
public class SignatureValidator {

    @Autowired
    private CertProperties certProperties;

    public  PublicKey loadSenderPublicKey() {
        try {
            final KeyStore keyStore = KeyStore.getInstance(certProperties.getPapssPublicKeyType());
            InputStream is = SignatureValidator.class.getResourceAsStream(certProperties.getPapssPublicStoreFile());
            keyStore.load(is, certProperties.getPapssPublicPassword().toCharArray());
            final Certificate certificate = keyStore.getCertificate(certProperties.getPapssPublicAlias());
            return certificate.getPublicKey();
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void verifySignatureForClient(final HttpServletRequest request, final String url) {
        final ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
        final String signatureHeader = request.getHeader("signature");

        if (signatureHeader == null) {
            throw new InvalidSecurityDetailsException("Invalid signature");
        }
        final byte[] decodedSignature = Base64.getDecoder().decode(signatureHeader);
        final String verificationText = url + new String(requestWrapper.getContentAsByteArray());
        log.info("verifying {}", verificationText);

        try {
            final Signature signature = Signature.getInstance("SHA256withRSA");

            signature.initVerify(loadSenderPublicKey());
            signature.update(verificationText.getBytes(StandardCharsets.UTF_8));
            if (!signature.verify(decodedSignature)) {
                throw new InvalidSecurityDetailsException("Invalid signature");
            }
        }

        catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void verifySignatureForClientAlternative(final HttpServletRequest request, final String url) {
        final ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
        final byte[] decodedSignature = Base64.getDecoder().decode(request.getHeader("signature"));
        final String verificationText = url + new String(requestWrapper.getContentAsByteArray());
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, loadSenderPublicKey());
            final byte[] decryptedMessageHash = cipher.doFinal(decodedSignature);

            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            final byte[] newMessageHash = md.digest(verificationText.getBytes(StandardCharsets.UTF_8));
            if (!Arrays.equals(decryptedMessageHash, newMessageHash)) {
                throw new java.security.SignatureException("Invalid signature");
            }
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
