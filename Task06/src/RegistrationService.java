import java.sql.SQLException;
import java.util.List;

/** Application layer: validates requests before delegating to JDBC repository. */
public class RegistrationService {
    private final RegistrationRepository repository;

    public RegistrationService(RegistrationRepository repository) {
        this.repository = repository;
    }

    public void register(String studentId, String studentName, String eventName)
            throws SQLException {
        String id = studentId == null ? "" : studentId.trim();
        String name = studentName == null ? "" : studentName.trim();
        String event = eventName == null ? "" : eventName.trim();

        if (id.isEmpty() || name.isEmpty() || event.isEmpty()) {
            throw new IllegalArgumentException("Student ID, name and event are required.");
        }
        if (!id.matches("[A-Za-z0-9-]{3,20}")) {
            throw new IllegalArgumentException(
                    "Student ID must contain 3–20 letters, digits or hyphens.");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Student name must be 100 characters or fewer.");
        }
        repository.insert(id, name, event);
    }

    public List<Registration> loadPage(String event, int page, int pageSize)
            throws SQLException {
        if (page < 0 || pageSize < 1) throw new IllegalArgumentException("Invalid page.");
        return repository.findPage(event, pageSize, page * pageSize);
    }

    public List<Registration> loadAll(String event) throws SQLException {
        return repository.findAll(event);
    }
}
