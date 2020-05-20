package projeto_1.user.exceptions;

public class DuplicateUserException extends Exception {
    private static final long serialVersionUID = -7738883640777499272L;
    public final String email;

    public DuplicateUserException(String email) {
        super("An user with email " + email + " already exists");
        this.email = email;
    }
}