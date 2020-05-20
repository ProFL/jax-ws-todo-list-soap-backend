package projeto_1;

import jakarta.annotation.PostConstruct;
import projeto_1.exceptions.InternalServerErrorException;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Repository<T extends Object> {
    protected final Class<T> entityClass;

    protected final String tableName;
    protected final Connection connection;

    public Repository(Class<T> entityClass, String tableName, Connection connection) {
        this.tableName = tableName;
        this.connection = connection;
        this.entityClass = entityClass;
    }

    @PostConstruct
    public abstract void assertTable();

    protected abstract T updateEntityFromResultSet(ResultSet rs, T entity) throws SQLException;

    protected T createEntityFromResultSet(ResultSet rs) throws SQLException, InternalServerErrorException {
        try {
            Constructor<T> constructor = this.entityClass.getConstructor();
            return this.updateEntityFromResultSet(rs, constructor.newInstance());
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("Failed to instantiate entity from result set");
        }
    }

    public T findById(int id) throws InternalServerErrorException {
        try (PreparedStatement st = this.connection.prepareStatement(
                "SELECT * FROM " + this.tableName + " WHERE " + this.tableName + ".id = ?"
        )) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    return createEntityFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
        return null;
    }

    public abstract T createOne(T entity) throws InternalServerErrorException;

    public abstract T replaceOne(T entity) throws InternalServerErrorException;

    public void deleteOne(int id) throws InternalServerErrorException {
        try (PreparedStatement st = connection.prepareStatement(
                "DELETE FROM " + tableName + " WHERE " + tableName + ".id = ?"
        )) {
            st.setInt(1, id);
            int updated = st.executeUpdate();
            System.out.println("Query: " + st.toString() + "\nDeleted: " + updated + " entries");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException("Failed to delete entity from " + tableName + " with id: " + id);
        }
    }
}
