package projeto_1.user.auth.exceptions;

public class UnauthorizedException extends Exception {
    public UnauthorizedException() {
        super("You need to be authenticated to proceed");
    }
}
