package za.co.quadrantsystems.message.signer;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.X509ExtendedKeyManager;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Slf4j
public class SelectableAliasKeyManager extends X509ExtendedKeyManager {
    private X509ExtendedKeyManager internalKeyManager;
    private String selectedAlias;

    public SelectableAliasKeyManager(X509ExtendedKeyManager originalKeyManager, String selectedAlias) {
        this.internalKeyManager = originalKeyManager;
        this.selectedAlias = selectedAlias;
    }

    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        if (this.internalKeyManager.getPrivateKey(this.selectedAlias) != null) {
            return this.selectedAlias;
        } else {
            String alias = this.internalKeyManager.chooseClientAlias(keyType, issuers, socket);
            log.error("Cannot select privateKey with alias: " + this.selectedAlias + ". Fallback to: " + alias);
            return alias;
        }
    }

    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        return this.internalKeyManager.chooseServerAlias(keyType, issuers, socket);
    }

    public X509Certificate[] getCertificateChain(String alias) {
        return this.internalKeyManager.getCertificateChain(alias);
    }

    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return this.internalKeyManager.getClientAliases(keyType, issuers);
    }

    public PrivateKey getPrivateKey(String alias) {
        return this.internalKeyManager.getPrivateKey(alias);
    }

    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return this.internalKeyManager.getServerAliases(keyType, issuers);
    }
}
