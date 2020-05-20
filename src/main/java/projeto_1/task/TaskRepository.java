package projeto_1.task;

import projeto_1.Repository;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.task.beans.Task;

import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;

public class TaskRepository extends Repository<Task> {
    @Inject
    public TaskRepository(Connection connection) {
        super(Task.class, "tasks", connection);
    }

    @Override
    public void assertTable() {
        System.out.println("Ensuring tasks table exists...");
        try (Statement st = this.connection.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS " + this.tableName + "("
                    + "id SERIAL PRIMARY KEY,"
                    + "ownerId int,"
                    + "name VARCHAR,"
                    + "description VARCHAR,"
                    + "CONSTRAINT owner_fk FOREIGN KEY (ownerId) REFERENCES users(id))");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to create the tasks table!");
        }
    }

    @Override
    protected Task updateEntityFromResultSet(ResultSet rs, Task entity) throws SQLException {
        return new Task() {{
            short failCount = 0;
            try {
                this.setId(rs.getInt("id"));
            } catch (SQLException ignored) {
                failCount += 1;
            }
            try {
                this.setOwnerId(rs.getInt("ownerId"));
            } catch (SQLException ignored) {
                failCount += 1;
            }
            try {
                this.setName(rs.getString("name"));
            } catch (SQLException ignored) {
                failCount += 1;
            }
            try {
                this.setDescription(rs.getString("description"));
            } catch (SQLException e) {
                if (failCount == 3) {
                    throw e;
                }
            }
        }};
    }

    public Task[] findByOwnerId(int ownerId) throws InternalServerErrorException {
        ArrayList<Task> tasks = new ArrayList<>();
        try (PreparedStatement st = this.connection.prepareStatement(
                "SELECT * FROM " + this.tableName
                        + " WHERE " + this.tableName + ".ownerId = ?"
        )) {
            st.setInt(1, ownerId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    tasks.add(this.createEntityFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
        return (Task[]) tasks.toArray();
    }

    @Override
    public Task createOne(Task task) throws InternalServerErrorException {
        try (PreparedStatement st = this.connection
                .prepareStatement("INSERT INTO " + this.tableName
                        + "(ownerId, name, description) VALUES(?, ?, ?) RETURNING id"
                )) {
            st.setInt(1, task.getId());
            st.setString(2, task.getName());
            st.setString(3, task.getDescription());

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    return this.updateEntityFromResultSet(rs, task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
        throw new InternalServerErrorException("Unexpected error while creating user");
    }

    @Override
    public Task replaceOne(Task entity) throws InternalServerErrorException {
        try (PreparedStatement st = this.connection
                .prepareStatement("UPDATE " + this.tableName
                        + " SET name = ?, description = ? WHERE id = ? RETURNING *"
                )) {
            st.setString(1, entity.getName());
            st.setString(2, entity.getDescription());
            st.setInt(3, entity.getId());

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    return this.createEntityFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
        throw new InternalServerErrorException("Unexpected error, failed to iterate over replace result?");
    }
}