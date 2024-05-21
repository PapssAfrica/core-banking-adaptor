package za.co.quadrantsystems.outbound.exception;

public class SignatureValidationException extends Exception {
    private static final long serialVersionUID = -3029378963239025784L;

    public SignatureValidationException(String message) {
        super(message);
    }

    public SignatureValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
