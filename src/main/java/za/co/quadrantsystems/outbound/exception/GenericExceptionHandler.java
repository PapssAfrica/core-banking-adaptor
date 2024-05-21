package za.co.quadrantsystems.outbound.exception;

public class GenericExceptionHandler extends RuntimeException {
    public GenericExceptionHandler(String message) {
        super(message);
    }
}
