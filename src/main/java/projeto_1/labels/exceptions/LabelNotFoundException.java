package projeto_1.labels.exceptions;

public class LabelNotFoundException extends Exception {
    private static final long serialVersionUID = -3903398314556592448L;
    public final int id;

    public LabelNotFoundException(int id) {
        super("Label with id: " + id + " was not found");
        this.id = id;
    }
}
