import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** AWT view: collects input and displays service results. */
public class FeeWindow extends Frame {
    private final TextField regNo = new TextField(18);
    private final TextField amount = new TextField(18);
    private final Label status = new Label("Enter details and select an action.");
    private final FeeService service = new FeeService();

    public FeeWindow() {
        super("Student Fee Payment");
        setLayout(new BorderLayout(8, 8));

        Panel fields = new Panel(new GridLayout(2, 2, 8, 8));
        fields.add(new Label("Registration number:"));
        fields.add(regNo);
        fields.add(new Label("Amount (Rs):"));
        fields.add(amount);

        Button pay = new Button("Pay");
        Button clear = new Button("Clear");
        Button receipt = new Button("Receipt");
        Panel actions = new Panel(new FlowLayout());
        actions.add(pay);
        actions.add(clear);
        actions.add(receipt);

        add(fields, BorderLayout.NORTH);
        add(actions, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        pay.addActionListener(e -> performPayment());
        clear.addActionListener(e -> clearFields());
        receipt.addActionListener(e -> showReceipt());

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setSize(430, 190);
        setLocationRelativeTo(null);
    }

    private void performPayment() {
        try {
            status.setText(service.pay(regNo.getText(), amount.getText()));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            status.setText(ex.getMessage());
        }
    }

    private void clearFields() {
        regNo.setText("");
        amount.setText("");
        status.setText("Fields cleared.");
    }

    private void showReceipt() {
        status.setText(service.receipt(regNo.getText()));
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FeeWindow().setVisible(true));
    }
}
