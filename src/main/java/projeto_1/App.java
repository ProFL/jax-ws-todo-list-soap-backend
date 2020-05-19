package projeto_1;

import jakarta.xml.ws.Endpoint;
import projeto_1.config.ConfigProvider;
import projeto_1.config.DbConnFactory;
import projeto_1.user.UserRepository;
import projeto_1.user.UserServiceImpl;
import projeto_1.user.auth.AuthServiceImpl;

import java.sql.Connection;

public class App {
    public static void main(String[] args) {
        try {
            ConfigProvider configs = ConfigProvider.getInstance();
            String port = configs.getPort();
            Connection dbConn = new DbConnFactory()
                    .setUser(configs.getDbUser())
                    .setPassword(configs.getDbPasswd())
                    .setConnectionString(configs.getDbConnStr())
                    .build();
            UserRepository userRepo = new UserRepository(dbConn);

            UserServiceImpl userService = new UserServiceImpl(userRepo);
            AuthServiceImpl authService = new AuthServiceImpl(userRepo);

            Endpoint.publish("http://0.0.0.0:" + port + "/user", userService);
            Endpoint.publish("http://0.0.0.0:" + port + "/user/auth", authService);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}