package projeto_1.labels_tasks;

import projeto_1.Repository;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.labels.LabelRepository;
import projeto_1.labels.beans.Label;
import projeto_1.labels_tasks.beans.LabelsTasks;
import projeto_1.task.TaskRepository;
import projeto_1.task.beans.Task;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.*;
import java.util.ArrayList;

@Singleton
public class LabelsTasksRepository extends Repository<LabelsTasks> {
    public static final String tableName = "labels_tasks";

    private final TaskRepository taskRepository;
    private final LabelRepository labelRepository;

    @Inject
    public LabelsTasksRepository(Connection connection, TaskRepository taskRepository, LabelRepository labelRepository) {
        super(LabelsTasks.class, tableName, connection);
        this.taskRepository = taskRepository;
        this.labelRepository = labelRepository;
    }

    @Override
    public void assertTable() {
        System.out.println("Ensuring " + tableName + " table exists...");
        try (Statement st = this.connection.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS " + tableName + '(' +
                    "taskId int NOT NULL" + ',' +
                    "labelId int NOT NULL" + ',' +
                    "CONSTRAINT task_fk FOREIGN KEY (taskId) REFERENCES " + TaskRepository.tableName + "(id) ON DELETE CASCADE" + ',' +
                    "CONSTRAINT label_fk FOREIGN KEY (labelId) REFERENCES " + LabelRepository.tableName + "(id) ON DELETE CASCADE" + ')');
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to create the " + tableName + " table!");
        }
    }

    @Override
    public LabelsTasks updateEntityFromResultSet(ResultSet rs, LabelsTasks entity) throws SQLException {
        entity.setTaskId(rs.getInt("taskId"));
        entity.setLabelId(rs.getInt("labelId"));
        return entity;
    }

    public Label[] findByTaskId(int taskId) throws InternalServerErrorException {
        ArrayList<Label> labels = new ArrayList<>();
        try (PreparedStatement st = this.connection.prepareStatement(
                "SELECT * FROM " + tableName +
                        " WHERE " + tableName + ".taskId = ? " +
                        "JOIN " + LabelRepository.tableName +
                        " ON " + tableName + ".labelId = " + LabelRepository.tableName + ".id"
        )) {
            st.setInt(1, taskId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    labels.add(labelRepository.createEntityFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
        Label[] arr = new Label[labels.size()];
        labels.toArray(arr);
        return arr;
    }

    public Task[] findByLabelId(int labelId) throws InternalServerErrorException {
        ArrayList<Task> tasks = new ArrayList<>();
        try (PreparedStatement st = this.connection.prepareStatement(
                "SELECT * FROM " + tableName +
                        " WHERE " + tableName + ".labelId = ? " +
                        "JOIN " + TaskRepository.tableName +
                        " ON " + tableName + ".taskId = " + TaskRepository.tableName + ".id"
        )) {
            st.setInt(1, labelId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    tasks.add(taskRepository.createEntityFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
        Task[] arr = new Task[tasks.size()];
        tasks.toArray(arr);
        return arr;
    }

    @Override
    public LabelsTasks createOne(LabelsTasks entity) throws InternalServerErrorException {
        try (PreparedStatement st = this.connection
                .prepareStatement("INSERT INTO " + tableName
                        + "(taskId, labelId) VALUES(?, ?) RETURNING *"
                )) {
            st.setInt(1, entity.getTaskId());
            st.setInt(2, entity.getLabelId());

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    return this.updateEntityFromResultSet(rs, entity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
        throw new InternalServerErrorException("Unexpected error while creating label_task relationship");
    }

    public void deleteOne(LabelsTasks entity) throws InternalServerErrorException {
        try (PreparedStatement st = this.connection
                .prepareStatement("DELETE FROM " + tableName
                        + "WHERE " + tableName + ".taskId = ? AND " + tableName + ".labelId = ?"
                )) {
            st.setInt(1, entity.getTaskId());
            st.setInt(2, entity.getLabelId());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
    }

    @Override
    public LabelsTasks findById(int id) throws InternalServerErrorException {
        throw new InternalServerErrorException("Invalid operation");
    }

    @Override
    public LabelsTasks replaceOne(LabelsTasks entity) throws InternalServerErrorException {
        throw new InternalServerErrorException("Invalid operation");
    }

    @Override
    public void deleteOne(int id) throws InternalServerErrorException {
        throw new InternalServerErrorException("Invalid operation");
    }
}
