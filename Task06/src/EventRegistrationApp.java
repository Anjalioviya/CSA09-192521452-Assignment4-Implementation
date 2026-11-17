import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * Layered campus event registration GUI.
 * Database work is performed in SwingWorker, not on the EDT.
 */
public class EventRegistrationApp extends JFrame {
    private static final int PAGE_SIZE = 25;

    private final JTextField studentId = new JTextField();
    private final JTextField studentName = new JTextField();
    private final JComboBox<String> eventChoice = new JComboBox<>(
            new String[]{"Tech Symposium", "Cultural Fest", "Sports Meet", "Hackathon"});
    private final JComboBox<String> filter = new JComboBox<>(
            new String[]{"All", "Tech Symposium", "Cultural Fest", "Sports Meet", "Hackathon"});
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Student ID", "Student Name", "Event", "Registered At"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel status = new JLabel("Ready.");
    private final JButton registerButton = new JButton("Register");
    private final RegistrationService service =
            new RegistrationService(new RegistrationRepository());
    private int page = 0;

    public EventRegistrationApp() {
        super("Campus Event Registration");
        setLayout(new BorderLayout(8, 8));
        setJMenuBar(createMenuBar());

        JPanel form = new JPanel(new GridLayout(3, 2, 6, 6));
        form.add(new JLabel("Student ID:"));
        form.add(studentId);
        form.add(new JLabel("Student name:"));
        form.add(studentName);
        form.add(new JLabel("Event:"));
        form.add(eventChoice);

        JPanel formActions = new JPanel();
        formActions.add(registerButton);
        JButton clear = new JButton("Clear");
        formActions.add(clear);

        JPanel north = new JPanel(new BorderLayout());
        north.add(form, BorderLayout.CENTER);
        north.add(formActions, BorderLayout.SOUTH);

        JPanel tableControls = new JPanel();
        tableControls.add(new JLabel("Filter event:"));
        tableControls.add(filter);
        JButton applyFilter = new JButton("Apply Filter");
        JButton previous = new JButton("Previous");
        JButton next = new JButton("Next");
        tableControls.add(applyFilter);
        tableControls.add(previous);
        tableControls.add(next);

        JPanel center = new JPanel(new BorderLayout());
        center.add(tableControls, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        add(north, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> register());
        clear.addActionListener(e -> {
            studentId.setText("");
            studentName.setText("");
            status.setText("Form cleared.");
        });
        applyFilter.addActionListener(e -> {
            page = 0;
            loadPage();
        });
        previous.addActionListener(e -> {
            if (page > 0) {
                page--;
                loadPage();
            }
        });
        next.addActionListener(e -> {
            page++;
            loadPage();
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        loadPage();
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem export = new JMenuItem("Export CSV");
        JMenuItem exit = new JMenuItem("Exit");
        export.addActionListener(e -> exportCsv());
        exit.addActionListener(e -> dispose());
        file.add(export);
        file.addSeparator();
        file.add(exit);
        bar.add(file);
        return bar;
    }

    private void register() {
        final String id = studentId.getText();
        final String name = studentName.getText();
        final String event = (String) eventChoice.getSelectedItem();

        setBusy(true, "Validating and registering...");
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                service.register(id, name, event);
                return null;
            }

            @Override protected void done() {
                setBusy(false, "Ready.");
                try {
                    get();
                    studentId.setText("");
                    studentName.setText("");
                    status.setText("Registration saved.");
                    page = 0;
                    loadPage();
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() == null ? ex : ex.getCause();
                    String message = cause instanceof SQLException
                            ? "Registration could not be saved. The student may already be registered for this event."
                            : cause.getMessage();
                    status.setText(message);
                    JOptionPane.showMessageDialog(EventRegistrationApp.this, message,
                            "Registration error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void loadPage() {
        String selected = (String) filter.getSelectedItem();
        setBusy(true, "Loading registrations...");
        new SwingWorker<List<Registration>, Void>() {
            @Override protected List<Registration> doInBackground() throws Exception {
                return service.loadPage(selected, page, PAGE_SIZE);
            }

            @Override protected void done() {
                try {
                    List<Registration> rows = get();
                    model.setRowCount(0);
                    for (Registration row : rows) {
                        model.addRow(new Object[]{row.getId(), row.getStudentId(),
                                row.getStudentName(), row.getEventName(), row.getRegisteredAt()});
                    }
                    status.setText("Loaded " + rows.size() + " registration(s), page "
                            + (page + 1) + ".");
                } catch (Exception ex) {
                    status.setText("Could not load data. Check MySQL configuration.");
                } finally {
                    setBusy(false, status.getText());
                }
            }
        }.execute();
    }

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("registrations.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String selected = (String) filter.getSelectedItem();
        setBusy(true, "Exporting registrations...");
        new SwingWorker<List<Registration>, Void>() {
            @Override protected List<Registration> doInBackground() throws Exception {
                return service.loadAll(selected);
            }

            @Override protected void done() {
                try {
                    List<Registration> rows = get();
                    writeCsv(chooser.getSelectedFile().toPath(), rows);
                    status.setText("Exported " + rows.size() + " row(s).");
                } catch (Exception ex) {
                    status.setText("Export failed: " + ex.getMessage());
                    JOptionPane.showMessageDialog(EventRegistrationApp.this,
                            "Export failed: " + ex.getMessage(), "Export error",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    setBusy(false, status.getText());
                }
            }
        }.execute();
    }

    private void writeCsv(Path path, List<Registration> rows) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("ID,Student ID,Student Name,Event,Registered At");
            writer.newLine();
            for (Registration row : rows) {
                writer.write(row.getId() + "," + csv(row.getStudentId()) + ","
                        + csv(row.getStudentName()) + "," + csv(row.getEventName()) + ","
                        + csv(row.getRegisteredAt()));
                writer.newLine();
            }
        }
    }

    private String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private void setBusy(boolean busy, String message) {
        registerButton.setEnabled(!busy);
        status.setText(message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EventRegistrationApp().setVisible(true));
    }
}
