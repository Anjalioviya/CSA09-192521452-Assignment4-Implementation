import java.util.HashSet;
import java.util.Set;

/**
 * Application/service layer for the fee window.
 * This teaching implementation simulates payment and keeps UI code separate.
 */
public class FeeService {
    private final Set<String> paidRegistrations = new HashSet<>();

    public String pay(String registrationNumber, String amountText) {
        String regNo = registrationNumber == null ? "" : registrationNumber.trim();
        String rawAmount = amountText == null ? "" : amountText.trim();

        if (regNo.isEmpty()) {
            throw new IllegalArgumentException("Enter the student registration number.");
        }
        if (rawAmount.isEmpty()) {
            throw new IllegalArgumentException("Enter the payment amount.");
        }

        final double amount;
        try {
            amount = Double.parseDouble(rawAmount);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Amount must be a valid number.");
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        if (paidRegistrations.contains(regNo)) {
            throw new IllegalStateException("A payment is already recorded for " + regNo + ".");
        }

        // Replace this simulation with a payment gateway/repository in a real system.
        paidRegistrations.add(regNo);
        return String.format("Payment successful for %s. Amount: Rs %.2f", regNo, amount);
    }

    public String receipt(String registrationNumber) {
        String regNo = registrationNumber == null ? "" : registrationNumber.trim();
        if (regNo.isEmpty()) {
            throw new IllegalArgumentException("Enter a registration number to view a receipt.");
        }
        if (!paidRegistrations.contains(regNo)) {
            return "No payment record found for " + regNo + ".";
        }
        return "Receipt: payment recorded for student " + regNo + ".";
    }
}
