import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Data-access layer. All user-provided values are passed as SQL parameters. */
public class RegistrationRepository {
    public void insert(String studentId, String studentName, String eventName)
            throws SQLException {
        String sql = "INSERT INTO registrations (student_id, student_name, event_name) "
                   + "VALUES (?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.setString(2, studentName);
            statement.setString(3, eventName);
            statement.executeUpdate();
        }
    }

    public List<Registration> findPage(String eventFilter, int limit, int offset)
            throws SQLException {
        String sql = "SELECT id, student_id, student_name, event_name, registered_at "
                   + "FROM registrations "
                   + "WHERE (? = 'All' OR event_name = ?) "
                   + "ORDER BY id DESC LIMIT ? OFFSET ?";
        List<Registration> rows = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, eventFilter);
            statement.setString(2, eventFilter);
            statement.setInt(3, limit);
            statement.setInt(4, offset);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Registration(
                            rs.getLong("id"), rs.getString("student_id"),
                            rs.getString("student_name"), rs.getString("event_name"),
                            rs.getTimestamp("registered_at").toString()));
                }
            }
        }
        return rows;
    }

    public List<Registration> findAll(String eventFilter) throws SQLException {
        String sql = "SELECT id, student_id, student_name, event_name, registered_at "
                   + "FROM registrations WHERE (? = 'All' OR event_name = ?) "
                   + "ORDER BY id DESC";
        List<Registration> rows = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, eventFilter);
            statement.setString(2, eventFilter);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Registration(
                            rs.getLong("id"), rs.getString("student_id"),
                            rs.getString("student_name"), rs.getString("event_name"),
                            rs.getTimestamp("registered_at").toString()));
                }
            }
        }
        return rows;
    }
}
