package projeto_1.exceptions;

public class InternalServerErrorException extends Exception {
    private static final long serialVersionUID = -9102396380703948391L;

    public InternalServerErrorException(String message) {
        super(message);
    }
}