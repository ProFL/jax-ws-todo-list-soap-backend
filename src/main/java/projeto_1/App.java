package projeto_1;

import javax.xml.ws.Endpoint;

import projeto_1.user.UserServiceImpl;
import projeto_1.user.auth.AuthServiceImpl;

public class App {
    public static void main(String[] args) {
        UserServiceImpl userService = new UserServiceImpl();
        AuthServiceImpl authService = new AuthServiceImpl(userService);

        Endpoint.publish("http://0.0.0.0:9876/user", userService);
        Endpoint.publish("http://0.0.0.0:9876/user/auth", authService);
    }
}
