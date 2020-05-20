package projeto_1.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.inject.Inject;
import jakarta.annotation.PostConstruct;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.user.beans.User;

import javax.inject.Singleton;
import java.sql.*;

@Singleton
public class UserRepository {
    private final Connection connection;

    @Inject
    public UserRepository(Connection connection) {
        this.connection = connection;
    }

    @PostConstruct
    public void createTable() {
        System.out.println("Ensuring users table exists...");
        try (Statement st = this.connection.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS users(" + "id SERIAL PRIMARY KEY," + "name VARCHAR,"
                    + "email VARCHAR," + "password VARCHAR," + "CONSTRAINT unique_email UNIQUE (email))");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to create the users table!");
        }
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    private User updateUserFromResultSet(ResultSet rs, User user) throws SQLException {
        short failCount = 0;
        try {
            user.setId(rs.getInt("id"));
        } catch (SQLException ignored) {
            failCount += 1;
        }
        try {
            user.setName(rs.getString("name"));
        } catch (SQLException ignored) {
            failCount += 1;
        }
        try {
            user.setEmail(rs.getString("email"));
        } catch (SQLException ignored) {
            failCount += 1;
        }
        try {
            user.setPassword(rs.getString("password"));
        } catch (SQLException e) {
            if (failCount == 3) {
                throw e;
            }
        }
        return user;
    }

    private User buildUserFromResultSet(ResultSet rs) throws SQLException {
        return this.updateUserFromResultSet(rs, new User());
    }

    public User findById(int id) throws InternalServerErrorException {
        try (PreparedStatement st = this.connection.prepareStatement("SELECT * FROM users WHERE users.id = ?")) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    return buildUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
        return null;
    }

    public User findByEmail(String email) throws InternalServerErrorException {
        try (PreparedStatement st = this.connection.prepareStatement("SELECT * FROM users WHERE users.email = ?")) {
            st.setString(1, email);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    return buildUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
        return null;
    }

    public User createOne(User user) throws InternalServerErrorException {
        try (PreparedStatement st = this.connection
                .prepareStatement("INSERT INTO users(name, email, password) VALUES(?, ?, ?) RETURNING id, password")) {
            st.setString(1, user.getName());
            st.setString(2, user.getEmail());
            st.setString(3, this.hashPassword(user.getPassword()));

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    return this.updateUserFromResultSet(rs, user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
        throw new InternalServerErrorException("Unexpected error while creating user");
    }

    public User replaceOne(User user) throws InternalServerErrorException {
        try (PreparedStatement st = this.connection
                .prepareStatement("UPDATE users SET name = ?, email = ?, password = ? WHERE id = ? RETURNING *")) {
            st.setString(1, user.getName());
            st.setString(2, user.getEmail());
            st.setString(3, this.hashPassword(user.getPassword()));
            st.setInt(4, user.getId());

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    return this.buildUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
        throw new InternalServerErrorException("Unexpected error, failed to iterate over replace result?");
    }
}