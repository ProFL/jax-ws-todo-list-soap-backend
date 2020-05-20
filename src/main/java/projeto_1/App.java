package projeto_1;

import com.google.inject.Guice;
import com.google.inject.Injector;
import jakarta.xml.ws.Endpoint;
import projeto_1.config.ConfigProvider;
import projeto_1.config.ConnectionProvider;
import projeto_1.user.UserRepository;
import projeto_1.user.UserServiceImpl;
import projeto_1.user.auth.AuthServiceImpl;

import java.awt.*;

public class App {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ConfigProvider configs = ConfigProvider.getInstance();
            Injector injector = Guice.createInjector(configs, new ConnectionProvider());

            try {
                UserRepository userRepo = injector.getInstance(UserRepository.class);
                userRepo.assertTable();

                UserServiceImpl userService = injector.getInstance(UserServiceImpl.class);
                AuthServiceImpl authService = injector.getInstance(AuthServiceImpl.class);

                String baseUrl = "http://0.0.0.0:" + configs.getPort();
                System.out.println("Publishing services on: " + baseUrl);
                Endpoint.publish(baseUrl + "/user", userService);
                Endpoint.publish(baseUrl + "/user/auth", authService);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}