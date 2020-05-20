package projeto_1.exceptions;

public class ForbiddenException extends Exception {
    private static final long serialVersionUID = 6641915958074071155L;
    public final String action;

    public ForbiddenException(String action) {
        super("You're not allowed to perform this action");
        this.action = action;
    }
}
