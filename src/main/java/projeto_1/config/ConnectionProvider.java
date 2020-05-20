package projeto_1.config;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider extends AbstractModule {
    private static Connection connection;

    @Provides
    @Inject
    public Connection getConnection(
            @ConfigProvider.PostgresUser String user,
            @ConfigProvider.PostgresPassword String password,
            @ConfigProvider.ConnectionString String connectionString
    ) throws SQLException, ClassNotFoundException {
        if (ConnectionProvider.connection == null) {
            Class.forName("org.postgresql.Driver");
            ConnectionProvider.connection = DriverManager.getConnection(connectionString, user, password);
        }
        return ConnectionProvider.connection;
    }
}