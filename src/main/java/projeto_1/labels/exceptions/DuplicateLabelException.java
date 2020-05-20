package projeto_1.labels.exceptions;

public class DuplicateLabelException extends Exception {
    private static final long serialVersionUID = 3016589517084069055L;
    public final String name;
    public final String color;

    public DuplicateLabelException(String name, String color) {
        super("A label with name \"" + name + "\" and color \"" + color + "\" already exists");
        this.name = name;
        this.color = color;
    }
}
