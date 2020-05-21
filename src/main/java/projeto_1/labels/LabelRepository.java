package projeto_1.labels;

import projeto_1.Repository;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.labels.beans.Label;
import projeto_1.user.UserRepository;

import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;

public class LabelRepository extends Repository<Label> {
    public static final String tableName = "labels";

    @Inject
    public LabelRepository(Connection connection) {
        super(Label.class, tableName, connection);
    }

    @Override
    public void assertTable() {
        System.out.println("Ensuring " + tableName + " table exists...");
        try (Statement st = this.connection.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS " + tableName + '(' +
                    "id SERIAL PRIMARY KEY" + ',' +
                    "ownerId int NOT NULL" + ',' +
                    "name VARCHAR NOT NULL" + ',' +
                    "color VARCHAR NOT NULL DEFAULT '#000000'" + ',' +
                    "CONSTRAINT name_color_unique UNIQUE (ownerId, name, color)" + ',' +
                    "CONSTRAINT owner_fk FOREIGN KEY (ownerId) REFERENCES " + UserRepository.tableName + "(id) ON DELETE CASCADE" + ')');
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to create the " + tableName + " table!");
        }
    }

    @Override
    protected Label updateEntityFromResultSet(ResultSet rs, Label label) throws SQLException {
        final short criticalFailureCount = 3;
        short failCount = 0;
        try {
            label.setId(rs.getInt("id"));
        } catch (SQLException ignored) {
            failCount++;
        }
        try {
            label.setId(rs.getInt("ownerId"));
        } catch (SQLException ignored) {
            failCount++;
        }
        try {
            label.setName(rs.getString("name"));
        } catch (SQLException ignored) {
            failCount++;
        }
        try {
            label.setColor(rs.getString("color"));
        } catch (SQLException e) {
            if (failCount >= criticalFailureCount) {
                throw e;
            }
        }
        return label;
    }

    public Label[] findByOwnerId(int ownerId) throws InternalServerErrorException {
        ArrayList<Label> labels = new ArrayList<>();
        try (PreparedStatement st = this.connection.prepareStatement(
                "SELECT * FROM " + tableName
                        + " WHERE " + tableName + ".ownerId = ?"
        )) {
            st.setInt(1, ownerId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    labels.add(this.createEntityFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
        Label[] tasksArr = new Label[labels.size()];
        labels.toArray(tasksArr);
        return tasksArr;
    }

    @Override
    public Label createOne(Label label) throws InternalServerErrorException {
        try (PreparedStatement st = this.connection
                .prepareStatement("INSERT INTO " + tableName
                        + "(ownerId, name, color) VALUES(?, ?, ?) RETURNING id"
                )) {
            st.setInt(1, label.getOwnerId());
            st.setString(2, label.getName());
            st.setString(3, label.getColor());

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    return this.updateEntityFromResultSet(rs, label);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
        throw new InternalServerErrorException("Unexpected error while creating label");
    }

    @Override
    public Label replaceOne(Label label) throws InternalServerErrorException {
        try (PreparedStatement st = this.connection
                .prepareStatement("UPDATE " + tableName
                        + " SET name = ?, color = ? WHERE id = ? RETURNING *"
                )) {
            st.setString(1, label.getName());
            st.setString(2, label.getColor());
            st.setInt(3, label.getId());

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
