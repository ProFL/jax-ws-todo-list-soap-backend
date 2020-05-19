package projeto_1.user;

import jakarta.annotation.PostConstruct;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.user.beans.User;
import projeto_1.user.exceptions.UserNotFoundException;

import java.sql.*;

public class UserRepository {
    private final Connection connection;

    public UserRepository(Connection connection) {
        this.connection = connection;
    }

    @PostConstruct
    public void createTable() {
        try (Statement st = this.connection.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS users(" + "id SERIAL PRIMARY KEY," + "name VARCHAR,"
                    + "email VARCHAR," + "password VARCHAR," + "CONSTRAINT unique_email UNIQUE (email))");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to create the users table!");
        }
    }

    private User buildUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        return user;
    }

    public User findById(int id) throws UserNotFoundException, InternalServerErrorException {
        try (PreparedStatement st = this.connection.prepareStatement("SELECT * FROM users WHERE users.id = ?")) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    return buildUserFromResultSet(rs);
                }
            }
            throw new UserNotFoundException(id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public User findByEmail(String email) throws UserNotFoundException, InternalServerErrorException {
        try (PreparedStatement st = this.connection.prepareStatement("SELECT * FROM users WHERE users.email = ?")) {
            st.setString(1, email);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    return buildUserFromResultSet(rs);
                }
            }
            throw new UserNotFoundException(email);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public User createOne(User user) throws UserNotFoundException, InternalServerErrorException {
        try (PreparedStatement st = this.connection
                .prepareStatement("INSERT INTO users(name, email, password) VALUES(?, ?, ?) RETURNING id")) {
            st.setString(1, user.getName());
            st.setString(2, user.getEmail());
            st.setString(3, user.getPassword());

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    user.setId(rs.getInt("id"));
                    return user;
                }
            }
            throw new UserNotFoundException(user.getEmail());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public User replaceOne(User user) throws UserNotFoundException, InternalServerErrorException {
        try (PreparedStatement st = this.connection
                .prepareStatement("UPDATE users SET name = ?, email = ?, password = ? WHERE id = ? RETURNING *")) {
            st.setString(1, user.getName());
            st.setString(2, user.getEmail());
            st.setString(3, user.getPassword());
            st.setInt(4, user.getId());

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    return this.buildUserFromResultSet(rs);
                }
            }
            throw new UserNotFoundException(user.getEmail());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}