package projeto_1.auth.exceptions;

public class PasswordMismatchException extends Exception {
    private static final long serialVersionUID = -4144344188217282615L;
    public final String email;

    public PasswordMismatchException(String email) {
        super("Invalid password provided for this email: " + email);
        this.email = email;
    }
}