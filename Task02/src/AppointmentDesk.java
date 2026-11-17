import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/** Runnable prototype for the appointment-desk event model. */
public class AppointmentDesk extends JFrame {
    private final JTextField patientId = new JTextField(15);
    private final JComboBox<String> department = new JComboBox<>(
            new String[]{"Cardiology", "Orthopaedics", "Neurology"});
    private final JComboBox<String> doctor = new JComboBox<>();
    private final JComboBox<String> slot = new JComboBox<>(
            new String[]{"09:00", "10:00", "11:00", "14:00", "15:00"});
    private final JLabel status = new JLabel("Enter details to book.");
    private final JButton book = new JButton("Book Appointment");
    private boolean bookingInProgress;

    private final Map<String, List<String>> doctorMap = new LinkedHashMap<>();

    public AppointmentDesk() {
        super("Hospital Outpatient Appointment Desk");
        doctorMap.put("Cardiology", List.of("Dr. Kumar", "Dr. Meena"));
        doctorMap.put("Orthopaedics", List.of("Dr. Ravi", "Dr. Priya"));
        doctorMap.put("Neurology", List.of("Dr. Arun", "Dr. Divya"));

        JPanel fields = new JPanel(new GridLayout(4, 2, 8, 8));
        fields.add(new JLabel("Patient ID:"));
        fields.add(patientId);
        fields.add(new JLabel("Department:"));
        fields.add(department);
        fields.add(new JLabel("Doctor:"));
        fields.add(doctor);
        fields.add(new JLabel("Time slot:"));
        fields.add(slot);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.add(book, BorderLayout.WEST);
        bottom.add(status, BorderLayout.CENTER);
        add(new JLabel("Hospital Outpatient Appointment Desk"), BorderLayout.NORTH);
        add(fields, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        department.addActionListener(e -> refreshDoctors());
        book.addActionListener(e -> bookAppointment());
        refreshDoctors();

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                if (bookingInProgress) {
                    int choice = JOptionPane.showConfirmDialog(
                            AppointmentDesk.this,
                            "A booking is in progress. Close this window?",
                            "Confirm close", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) dispose();
                } else {
                    dispose();
                }
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(520, 260);
        setLocationRelativeTo(null);
    }

    private void refreshDoctors() {
        String selected = (String) department.getSelectedItem();
        doctor.removeAllItems();
        if (selected != null) {
            for (String name : doctorMap.getOrDefault(selected, List.of())) {
                doctor.addItem(name);
            }
        }
    }

    private void bookAppointment() {
        String id = patientId.getText().trim();
        if (id.isEmpty()) {
            status.setText("Error: enter patient ID.");
            patientId.requestFocusInWindow();
            return;
        }
        if (doctor.getSelectedItem() == null || slot.getSelectedItem() == null) {
            status.setText("Error: choose doctor and time slot.");
            return;
        }

        bookingInProgress = true;
        book.setEnabled(false);
        try {
            String message = "Appointment booked for " + id + " with "
                    + doctor.getSelectedItem() + " (" + department.getSelectedItem()
                    + ") at " + slot.getSelectedItem() + ".";
            status.setText(message);
            JOptionPane.showMessageDialog(this, message, "Booking confirmed",
                    JOptionPane.INFORMATION_MESSAGE);
        } finally {
            bookingInProgress = false;
            book.setEnabled(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppointmentDesk().setVisible(true));
    }
}
