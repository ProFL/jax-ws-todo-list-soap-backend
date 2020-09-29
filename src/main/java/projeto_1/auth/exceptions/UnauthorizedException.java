package projeto_1.auth.exceptions;

public class UnauthorizedException extends Exception {
    private static final long serialVersionUID = -5580136944147486446L;
    public final String extraInfo;

    public UnauthorizedException() {
        this(null);
    }

    public UnauthorizedException(String extraInfo) {
        super("You need to be authenticated to perform this action");
        this.extraInfo = extraInfo;
    }

    public UnauthorizedException(String extraInfo, Exception baseException) {
        super("You need to be authenticated to perform this action", baseException);
        this.extraInfo = extraInfo;
    }
}
