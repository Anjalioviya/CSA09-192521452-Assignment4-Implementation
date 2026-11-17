public class Registration {
    private final long id;
    private final String studentId;
    private final String studentName;
    private final String eventName;
    private final String registeredAt;

    public Registration(long id, String studentId, String studentName,
                        String eventName, String registeredAt) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.eventName = eventName;
        this.registeredAt = registeredAt;
    }

    public long getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getEventName() { return eventName; }
    public String getRegisteredAt() { return registeredAt; }
}
