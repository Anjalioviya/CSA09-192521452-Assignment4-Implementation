import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class AnonymousClassConverter extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    public AnonymousClassConverter() {
        super("Converter – Anonymous Inner Class");
        setLayout(new FlowLayout());
        JButton convert = new JButton("Convert");
        add(new JLabel("INR")); add(amount); add(target); add(convert); add(result);

        ActionListener handler = new ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                try {
                    double value = Double.parseDouble(amount.getText().trim());
                    if (!Double.isFinite(value) || value < 0) throw new NumberFormatException();
                    String currency = (String) target.getSelectedItem();
                    double rate = currency.equals("USD") ? 0.012 : 0.011;
                    String text = String.format("%.2f %s", value * rate, currency);
                    result.setText(text);
                    JOptionPane.showMessageDialog(AnonymousClassConverter.this, text);
                } catch (NumberFormatException ex) {
                    result.setText("Invalid amount");
                    JOptionPane.showMessageDialog(AnonymousClassConverter.this, "Invalid amount",
                            "Input error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        convert.addActionListener(handler);
        amount.addActionListener(handler);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose(); }
        });
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack(); setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AnonymousClassConverter().setVisible(true));
    }
}
