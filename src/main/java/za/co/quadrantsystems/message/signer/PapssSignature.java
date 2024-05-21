package za.co.quadrantsystems.message.signer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import za.co.quadrantsystems.outbound.exception.SignatureValidationException;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.xml.crypto.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


@Component
public class PapssSignature {
	protected Log logger = LogFactory.getLog(this.getClass());
	private List<X509Certificate> trustedCerts;
	@Autowired
	KeyStoreLoader keyStoreLoader;

	public String generateSignature(String xml, String keyAlias, String keyPass) throws SignatureException {
		try {
			KeyStore keyStore = keyStoreLoader.getKeyStore(keyAlias);
			Data data = new OctetStreamData(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
			return this.generateSignature(data, (X509Certificate) keyStore.getCertificate(keyAlias),
					(PrivateKey) keyStore.getKey(keyAlias, keyPass.toCharArray()));
		} catch (Exception var3) {
			throw new SignatureException("Failed signing xml", var3);
		}
	}


	private String generateSignature(Data data, X509Certificate signingCert, PrivateKey signingKey)
			throws SignatureException {
		XMLSignatureFactory fac = this.getXMLSignatureFactory();

		try {
			List<Transform> trfs = new ArrayList();
			trfs.add(fac.newTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature",
					(TransformParameterSpec) null));
			trfs.add(fac.newCanonicalizationMethod("http://www.w3.org/2006/12/xml-c14n11",
					(C14NMethodParameterSpec) null));
			Reference ref = fac.newReference("", fac.newDigestMethod("http://www.w3.org/2001/04/xmlenc#sha256", null),
					trfs, null, null);
			String sigAlg = "";
			if (signingKey instanceof ECPrivateKey) {
				sigAlg = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
			} else {
				if (!(signingKey instanceof RSAPrivateKey)) {
					throw new IllegalArgumentException("Uknown PrivateKeyType: " + signingKey.getClass());
				}

				sigAlg = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
			}

			SignedInfo si = fac.newSignedInfo(
					fac.newCanonicalizationMethod("http://www.w3.org/TR/2001/REC-xml-c14n-20010315",
							(C14NMethodParameterSpec) null),
					fac.newSignatureMethod(sigAlg, null), Collections.singletonList(ref));
			KeyInfoFactory kif = fac.getKeyInfoFactory();
			List<Object> x509Content = new ArrayList();
			x509Content.add(signingCert.getSubjectDN().getName());
			x509Content
					.add(kif.newX509IssuerSerial(signingCert.getIssuerDN().getName(), signingCert.getSerialNumber()));
			X509Data xd = kif.newX509Data(x509Content);
			KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			CanonicalizationMethod canonicalizationMethod = fac.newCanonicalizationMethod(
					"http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (C14NMethodParameterSpec) null);
			OctetStreamData transformedData = (OctetStreamData) canonicalizationMethod.transform(data, null);
			Document doc = dbf.newDocumentBuilder().parse(transformedData.getOctetStream());
			Node parentNode = null;
			NodeList parentList = doc.getElementsByTagNameNS("*", "Sgntr");
			// Node parentNode;
			if (parentList.getLength() == 0) {
				parentList = doc.getElementsByTagNameNS("*", "AppHdr");
				parentNode = doc.createElementNS(parentList.item(0).getFirstChild().getNextSibling().getNamespaceURI(),
						"Sgntr");
				parentNode = parentList.item(0).appendChild(parentNode);
			} else {
				parentNode = parentList.item(0);
			}

			DOMSignContext dsc = new DOMSignContext(signingKey, parentNode);
			XMLSignature signature = fac.newXMLSignature(si, ki);
			signature.sign(dsc);
			StringWriter swr = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.transform(new DOMSource(doc), new StreamResult(swr));
			return swr.toString();
		} catch (Exception var24) {
			throw new SignatureException("Error signing data", var24);
		}
	}

	public void validateSignature(Document xmlContent) throws SignatureValidationException {
		NodeList nl = xmlContent.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
		if (nl.getLength() == 0) {
			throw new SignatureValidationException("Cannot find Signature element");
		} else {
			XMLSignatureFactory fac = this.getXMLSignatureFactory();
			DOMValidateContext valContext = new DOMValidateContext(
					new PapssSignature.KeyValueKeySelector(this.trustedCerts), nl.item(0));
			boolean coreValidity = false;

			try {
				XMLSignature signature = fac.unmarshalXMLSignature(valContext);
				boolean fullyProtected = false;
				Iterator<Reference> var8 = signature.getSignedInfo().getReferences().iterator();

				label49: {
					Reference ref;
					do {
						if (!var8.hasNext()) {
							break label49;
						}

						Object o = var8.next();
						ref = (Reference) o;
					} while (ref.getURI() != null && !ref.getURI().isEmpty());

					fullyProtected = true;
				}

				if (!fullyProtected) {
					throw new SignatureValidationException("The signature was not protecting the whole message");
				}

				coreValidity = signature.validate(valContext);
				if (!coreValidity) {
					this.logger.warn("Signature failed core validation");
					boolean sv = signature.getSignatureValue().validate(valContext);
					this.logger.warn("signature validation status: " + sv);
					Iterator<?> i = signature.getSignedInfo().getReferences().iterator();

					for (int j = 0; i.hasNext(); ++j) {
						boolean refValid = ((Reference) i.next()).validate(valContext);
						this.logger.warn("ref[" + j + "] validity status: " + refValid);
					}
				} else {
					X509Certificate siginingCert = ((PapssSignature.SimpleKeySelectorResult) signature
							.getKeySelectorResult()).getCert();
					Date now = new Date();
					if (siginingCert.getNotAfter().before(now) || siginingCert.getNotBefore().after(now)
							|| !this.validateRevocation(siginingCert)) {
						throw new SignatureValidationException(
								"Signature validation failed. Certificate not valid anymore/yet");
					}
				}
			} catch (MarshalException var12) {
				this.logger.error("Failed validating XML signature", var12);
			} catch (XMLSignatureException var13) {
				this.logger.error("Failed validating XML signature", var13);
			}

			if (!coreValidity) {
				throw new SignatureValidationException("Signature validation failed");
			}
		}
	}

	protected boolean validateRevocation(X509Certificate cert) {
		return true;
	}

	protected XMLSignatureFactory getXMLSignatureFactory() {
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
		return fac;
	}

	private static class SimpleKeySelectorResult implements KeySelectorResult {
		private final X509Certificate cert;

		SimpleKeySelectorResult(X509Certificate cert) {
			this.cert = cert;
		}

		public X509Certificate getCert() {
			return this.cert;
		}

		public Key getKey() {
			return this.cert.getPublicKey();
		}
	}

	private static class KeyValueKeySelector extends KeySelector {
		List<X509Certificate> certs;

		public KeyValueKeySelector(List<X509Certificate> certs) {
			this.certs = certs;
		}

		public KeySelectorResult select(KeyInfo keyInfo, KeySelector.Purpose purpose, AlgorithmMethod method,
				XMLCryptoContext context) throws KeySelectorException {
			if (keyInfo == null) {
				throw new KeySelectorException("Null KeyInfo object!");
			} else {
				SignatureMethod sm = (SignatureMethod) method;
				List<?> list = keyInfo.getContent();

				for (int i = 0; i < list.size(); ++i) {
					XMLStructure xmlStructure = (XMLStructure) list.get(i);
					if (xmlStructure instanceof X509Data domx509Data) {
						X509Certificate sigCert = null;
						String dn = null;
						X509IssuerSerial serial = null;
						Iterator var13 = domx509Data.getContent().iterator();

						while (var13.hasNext()) {
							Object x509Data = var13.next();
							if (x509Data instanceof X509Certificate) {
								sigCert = (X509Certificate) x509Data;
							} else if (x509Data instanceof String) {
								dn = (String) x509Data;
							} else if (x509Data instanceof X509IssuerSerial) {
								serial = (X509IssuerSerial) x509Data;
							}
						}

						X509Certificate matchedCert = null;
						X509Certificate cert;
						Iterator var17;
						if (sigCert != null) {
							var17 = this.certs.iterator();

							while (var17.hasNext()) {
								cert = (X509Certificate) var17.next();
								if (cert.getSerialNumber().equals(sigCert.getSerialNumber())
										&& dnEquals(cert.getIssuerDN().getName(), sigCert.getIssuerDN().getName())
										&& dnEquals(sigCert.getSubjectDN().getName(), cert.getSubjectDN().getName())) {
									matchedCert = sigCert;
									break;
								}
							}
						} else if (serial != null) {
							label95: {
								var17 = this.certs.iterator();

								do {
									do {
										do {
											if (!var17.hasNext()) {
												break label95;
											}

											cert = (X509Certificate) var17.next();
										} while (!cert.getSerialNumber().equals(serial.getSerialNumber()));
									} while (!dnEquals(cert.getIssuerDN().getName(), serial.getIssuerName()));
								} while (dn != null && !dnEquals(dn, cert.getSubjectDN().getName()));

								matchedCert = cert;
							}
						}

						if (matchedCert == null) {
							throw new KeySelectorException(
									"Cannot retrieve certificate using DOMX509Data " + domx509Data);
						}

						if (algEquals(sm.getAlgorithm(), matchedCert.getPublicKey().getAlgorithm())) {
							return new PapssSignature.SimpleKeySelectorResult(matchedCert);
						}
					}
				}

				throw new KeySelectorException("No KeyValue element found!");
			}
		}

		private static boolean algEquals(String algURI, String algName) {
			if (algName.equalsIgnoreCase("DSA")
					&& algURI.equalsIgnoreCase("http://www.w3.org/2000/09/xmldsig#dsa-sha1")) {
				return true;
			} else if (algName.equalsIgnoreCase("RSA")
					&& algURI.equalsIgnoreCase("http://www.w3.org/2000/09/xmldsig#rsa-sha1")) {
				return true;
			} else if (algName.equalsIgnoreCase("EC")
					&& algURI.equalsIgnoreCase("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256")) {
				return true;
			} else {
				return algName.equalsIgnoreCase("RSA")
						&& algURI.equalsIgnoreCase("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
			}
		}

		private static boolean dnEquals(String dn1, String dn2) {
			if (dn1 == null) {
				return dn2 == null;
			} else if (dn2 == null) {
				return false;
			} else if (dn1.equals(dn2)) {
				return true;
			} else {
				List rdn1;
				List rdn2;
				try {
					rdn1 = (new LdapName(dn1)).getRdns();
					rdn2 = (new LdapName(dn2)).getRdns();
				} catch (InvalidNameException var5) {
					return false;
				}
				return rdn1.size() == rdn2.size() && rdn1.containsAll(rdn2);
			}
		}
	}
}
