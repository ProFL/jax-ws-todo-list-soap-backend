package projeto_1.exceptions;

public class InternalServerErrorException extends Exception {
    private static final long serialVersionUID = -9102396380703948391L;
    public final Exception innerException;

    public InternalServerErrorException(String errorMessage) {
        super("An unexpected error ocurred");
        this.innerException = new Exception(errorMessage);
    }

    public InternalServerErrorException(Exception innerException) {
        super("An unexpected error ocurred");
        this.innerException = innerException;
    }
}