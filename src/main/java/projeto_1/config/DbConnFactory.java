package projeto_1.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnFactory {
    private String user;
    private String password;
    private String connectionString;

    public Connection build() throws SQLException, ClassNotFoundException {
        if (user == null || password == null || connectionString == null) {
            throw new IllegalArgumentException("Insuficient arguments to build database connection");
        }
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(this.connectionString, this.user, this.password);
    }

    public DbConnFactory setUser(String user) {
        this.user = user;
        return this;
    }

    public DbConnFactory setPassword(String password) {
        this.password = password;
        return this;
    }

    public DbConnFactory setConnectionString(String connectionString) {
        this.connectionString = connectionString;
        return this;
    }
}