# Task 1 – GUI Design Review

## 1.1 Diagnosis of the supplied design

| Deficiency | Consequence |
|---|---|
| UI, validation, database and payment logic are all in one listener | The window is difficult to test and changes to payment logic affect the UI. |
| `==` is used for action-command strings | String identity is compared, not string content; the intended branch may not run. Use `.equals`. |
| No empty or numeric validation | Empty text or malformed amounts can cause exceptions and an invalid payment attempt. |
| JDBC work runs on the event-dispatch thread | The window freezes while the database is accessed. |
| `Thread.sleep(3000)` runs on the event-dispatch thread | The UI cannot repaint or respond for three seconds. |
| No clear success/failure feedback | The user cannot reliably tell whether payment succeeded. |
| Receipt button is not handled separately | Receipt requests are incorrectly treated like the Clear action. |
| SQL is built by concatenating user input | SQL injection and quoting errors are possible; use `PreparedStatement`. |
| No window-closing handler or connection lifecycle | The application may not close cleanly and database resources may leak. |
| Checked exceptions are not handled | Database failures are not reported or recovered from properly. |

## 1.2 Repaired structure

```text
FeeWindow (AWT view)
  ├── registration and amount fields
  ├── Pay / Clear / Receipt controls
  └── displays messages
        |
        v
FeeService (validation and payment use-case)
        |
        v
PaymentRepository / payment gateway (production extension)
```

The provided `FeeWindow` delegates payment and receipt operations to `FeeService`. It uses distinct action handlers, validates empty and invalid values, and disposes the window on close. The service currently simulates payment so it can run without external credentials.

## 1.3 Design defence (under 100 words)

The redesigned window only gathers user input and displays the outcome, while `FeeService` owns validation and payment rules. The service can be tested independently without opening a window, and the interface can be redesigned without rewriting payment rules. Separate handlers make each button's responsibility clear. A production version can add a repository or payment gateway behind the service and move slow work to a background worker, keeping the event-dispatch thread responsive.

## Alternative listener concept and complexity

A single listener using an `if–else` chain may perform up to `k` command comparisons for `k` buttons, so dispatch selection is O(k) in the worst case. Registering one listener per button or using one Swing `Action` avoids searching through all commands; dispatch is O(1) after the event source has selected its listener. The trade-off is O(k) listener/action objects and their small associated memory overhead. For a small form, either is acceptable; distinct handlers improve clarity as the UI grows.
