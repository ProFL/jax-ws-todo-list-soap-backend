package projeto_1.user.beans;

import java.io.Serializable;

public class Token implements Serializable {
    public String token;

    public Token() {
        this.token = "";
    }

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
