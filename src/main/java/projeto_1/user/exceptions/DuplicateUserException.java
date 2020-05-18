package projeto_1.user.exceptions;

public class DuplicateUserException extends Exception {
    final String email;

    public DuplicateUserException(String email) {
        super("An user with email " + email + " already exists");
        this.email = email;
    }
}