package projeto_1.user.exceptions;

public class UserNotFoundException extends Exception {
    private static final long serialVersionUID = -4234236577803739330L;
    final int id;
    final String email;

    public UserNotFoundException(int id) {
        super("There is no user with id: " + id);
        this.id = id;
        this.email = null;
    }

    public UserNotFoundException(String email) {
        super("There is no user with email: " + email);
        this.id = -1;
        this.email = email;
    }
}