import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/** Library search layout: centre results grow; toolbars retain preferred size. */
public class LibrarySearchScreen extends JFrame {
    private final JTextField searchField = new JTextField(22);
    private final JComboBox<String> category =
            new JComboBox<>(new String[]{"All", "Programming", "Networks", "Security"});
    private final JLabel status = new JLabel("Ready.");

    public LibrarySearchScreen() {
        super("Campus Library Search");
        setLayout(new BorderLayout(8, 8));

        JLabel title = new JLabel("Campus Library Search");
        title.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 10));
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchRow.add(new JLabel("Search:"));
        searchRow.add(searchField);
        searchRow.add(category);
        JButton search = new JButton("Search");
        searchRow.add(search);

        String[] columns = {"Book ID", "Title", "Category", "Availability"};
        Object[][] rows = {
            {"B101", "Java Programming", "Programming", "Available"},
            {"B102", "Computer Networks", "Networks", "Issued"},
            {"B103", "Cryptography Basics", "Security", "Available"},
            {"B104", "Data Structures", "Programming", "Available"}
        };
        DefaultTableModel model = new DefaultTableModel(rows, columns) {
            @Override public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable results = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(results);

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.NORTH);
        top.add(searchRow, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel navigation = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton previous = new JButton("Prev");
        JButton next = new JButton("Next");
        JButton reserve = new JButton("Reserve");
        navigation.add(previous);
        navigation.add(next);
        navigation.add(reserve);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(navigation, BorderLayout.CENTER);
        bottom.add(status, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        search.addActionListener(e -> status.setText(
                "Search requested: " + searchField.getText().trim()
                + " | Category: " + category.getSelectedItem()));
        previous.addActionListener(e -> status.setText("Previous page selected."));
        next.addActionListener(e -> status.setText("Next page selected."));
        reserve.addActionListener(e -> {
            int selected = results.getSelectedRow();
            status.setText(selected < 0 ? "Select a book to reserve."
                    : "Reservation requested for " + model.getValueAt(selected, 1) + ".");
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(560, 350));
        setSize(760, 500);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarySearchScreen().setVisible(true));
    }
}
