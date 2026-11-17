import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Centralises JDBC connection configuration; credentials are read from environment. */
public final class Database {
    private Database() {}

    public static Connection getConnection() throws SQLException {
        String url = System.getenv().getOrDefault(
                "EVENT_DB_URL", "jdbc:mysql://localhost:3306/campus_events");
        String user = System.getenv().getOrDefault("EVENT_DB_USER", "root");
        String password = System.getenv().getOrDefault("EVENT_DB_PASSWORD", "");
        return DriverManager.getConnection(url, user, password);
    }
}
