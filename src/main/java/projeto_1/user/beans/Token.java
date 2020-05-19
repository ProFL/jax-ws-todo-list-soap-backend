package projeto_1.user.beans;

import java.io.Serializable;

public class Token implements Serializable {
    public final String token;

    public Token(String token) {
        this.token = token;
    }
}
