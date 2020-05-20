package projeto_1.auth.exceptions;

public class UserNotFoundException extends Exception {
    private static final long serialVersionUID = 4987321165501905922L;
    public final String email;

    public UserNotFoundException(String email) {
        super("No user was found with the given email: " + email);
        this.email = email;
    }
}
