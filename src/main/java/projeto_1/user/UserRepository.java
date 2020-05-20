package projeto_1.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.inject.Inject;
import projeto_1.Repository;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.user.beans.User;

import javax.inject.Singleton;
import java.sql.*;

@Singleton
public class UserRepository extends Repository<User> {
    @Inject
    public UserRepository(Connection connection) {
        super(User.class, "users", connection);
    }

    @Override
    public void assertTable() {
        System.out.println("Ensuring users table exists...");
        try (Statement st = this.connection.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS " + this.tableName + "("
                    + "id SERIAL PRIMARY KEY,"
                    + "name VARCHAR NOT NULL,"
                    + "email VARCHAR NOT NULL,"
                    + "password VARCHAR NOT NULL,"
                    + "CONSTRAINT unique_email UNIQUE (email))");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to create the users table!");
        }
    }

    @Override
    protected User updateEntityFromResultSet(ResultSet rs, User user) throws SQLException {
        final short criticalFailureCount = 3;
        short failCount = 0;
        try {
            user.setId(rs.getInt("id"));
        } catch (SQLException ignored) {
            failCount++;
        }
        try {
            user.setName(rs.getString("name"));
        } catch (SQLException ignored) {
            failCount++;
        }
        try {
            user.setEmail(rs.getString("email"));
        } catch (SQLException ignored) {
            failCount++;
        }
        try {
            user.setPassword(rs.getString("password"));
        } catch (SQLException e) {
            if (failCount >= criticalFailureCount) {
                throw e;
            }
        }
        return user;
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public User findByEmail(String email) throws InternalServerErrorException {
        try (PreparedStatement st = this.connection.prepareStatement(
                "SELECT * FROM " + this.tableName
                        + " WHERE " + this.tableName + ".email = ?"
        )) {
            st.setString(1, email);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    return this.createEntityFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
        return null;
    }

    @Override
    public User createOne(User user) throws InternalServerErrorException {
        try (PreparedStatement st = this.connection
                .prepareStatement("INSERT INTO " + this.tableName
                        + "(name, email, password) VALUES(?, ?, ?) RETURNING id, password"
                )) {
            st.setString(1, user.getName());
            st.setString(2, user.getEmail());
            st.setString(3, this.hashPassword(user.getPassword()));

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    return this.updateEntityFromResultSet(rs, user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
        throw new InternalServerErrorException("Unexpected error while creating user");
    }

    @Override
    public User replaceOne(User user) throws InternalServerErrorException {
        try (PreparedStatement st = this.connection
                .prepareStatement("UPDATE " + this.tableName
                        + " SET name = ?, email = ?, password = ? WHERE id = ? RETURNING *"
                )) {
            st.setString(1, user.getName());
            st.setString(2, user.getEmail());
            st.setString(3, this.hashPassword(user.getPassword()));
            st.setInt(4, user.getId());

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    return this.createEntityFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
        throw new InternalServerErrorException("Unexpected error, failed to iterate over replace result?");
    }
}