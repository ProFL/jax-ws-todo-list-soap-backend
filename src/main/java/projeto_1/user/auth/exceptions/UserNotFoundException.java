package projeto_1.user.auth.exceptions;

public class UserNotFoundException extends Exception {
    public final String email;

    public UserNotFoundException(String email) {
        super("No user was found with the given email: " + email);
        this.email = email;
    }
}
