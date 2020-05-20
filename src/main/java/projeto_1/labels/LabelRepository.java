package projeto_1.labels;

import projeto_1.Repository;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.labels.beans.Label;

import javax.inject.Inject;
import java.sql.*;

public class LabelRepository extends Repository<Label> {
    @Inject
    public LabelRepository(Connection connection) {
        super(Label.class, "labels", connection);
    }

    @Override
    public void assertTable() {
        System.out.println("Ensuring labels table exists...");
        try (Statement st = this.connection.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS " + this.tableName + "("
                    + "id SERIAL PRIMARY KEY,"
                    + "ownerId int NOT NULL,"
                    + "name VARCHAR NOT NULL,"
                    + "color VARCHAR NOT NULL DEFAULT '#000000',"
                    + "CONSTRAINT name_color_unique UNIQUE (ownerId, name, color)"
                    + "CONSTRAINT owner_fk FOREIGN KEY (ownerId) REFERENCES users(id) ON DELETE CASCADE)");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to create the tasks table!");
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

    @Override
    public Label createOne(Label label) throws InternalServerErrorException {
        try (PreparedStatement st = this.connection
                .prepareStatement("INSERT INTO " + this.tableName
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
                .prepareStatement("UPDATE " + this.tableName
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