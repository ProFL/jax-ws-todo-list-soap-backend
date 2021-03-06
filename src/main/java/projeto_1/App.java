package projeto_1;

import com.google.inject.Guice;
import com.google.inject.Injector;
import jakarta.xml.ws.Endpoint;
import projeto_1.auth.AuthServiceImpl;
import projeto_1.config.ConfigProvider;
import projeto_1.config.ConnectionProvider;
import projeto_1.labels.LabelRepository;
import projeto_1.labels.LabelServiceImpl;
import projeto_1.labels_tasks.LabelsTasksRepository;
import projeto_1.task.TaskRepository;
import projeto_1.task.TaskServiceImpl;
import projeto_1.user.UserRepository;
import projeto_1.user.UserServiceImpl;

import java.awt.*;

public class App {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ConfigProvider configs = ConfigProvider.getInstance();
            Injector injector = Guice.createInjector(configs, new ConnectionProvider());

            try {
                for (Repository repo : new Repository[]{
                        injector.getInstance(UserRepository.class),
                        injector.getInstance(TaskRepository.class),
                        injector.getInstance(LabelRepository.class),
                        injector.getInstance(LabelsTasksRepository.class),
                }) {
                    repo.assertTable();
                }

                UserServiceImpl userService = injector.getInstance(UserServiceImpl.class);
                AuthServiceImpl authService = injector.getInstance(AuthServiceImpl.class);
                TaskServiceImpl taskService = injector.getInstance(TaskServiceImpl.class);
                LabelServiceImpl labelService = injector.getInstance(LabelServiceImpl.class);

                String baseUrl = "http://0.0.0.0:" + configs.getPort();
                System.out.println("Publishing services on: " + baseUrl);
                Endpoint.publish(baseUrl + "/user", userService);
                Endpoint.publish(baseUrl + "/auth", authService);
                Endpoint.publish(baseUrl + "/task", taskService);
                Endpoint.publish(baseUrl + "/labels", labelService);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}