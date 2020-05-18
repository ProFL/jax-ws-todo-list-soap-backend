package projeto_1.user.auth.exceptions;

public class PasswordMismatchException extends Exception {
    public final String email;

    public PasswordMismatchException(String email) {
        super("Invalid password provided for this email: " + email);
        this.email = email;
    }
}