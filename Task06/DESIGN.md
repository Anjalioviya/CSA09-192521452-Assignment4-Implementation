# Task 6 – Campus Event Registration Desktop Application

## Stage 1 – Interpret the problem

**Users**
- Student: enters student ID, name and event to register.
- Committee member: views registrations, filters by event and exports data.

**Screens**
- Registration form.
- Registration table with event filter and page navigation.
- File menu with CSV export and Exit.
- Dialogs for validation/database errors.

**Data**
- Student ID, student name, event name, generated registration ID and registration timestamp.

**Testable requirements**
1. The form must reject blank student ID, student name or event.
2. Student ID must contain 3–20 letters, digits or hyphens.
3. A student may register only once for the same event.
4. A successful registration must be persisted in MySQL.
5. The table must display registrations and filter by event.
6. Database operations must execute outside the Swing event-dispatch thread.
7. The user must be able to export matching registrations as CSV.
8. The table must load one page at a time rather than loading all 100,000 rows on initial display.

## Stage 2 – Screen and event flow

```text
Application starts
      |
      v
Registration / Listing JFrame
  |                  |
  | Register         | Filter / Previous / Next
  v                  v
Validate input     SwingWorker queries page
  |                  |
  v                  v
SwingWorker INSERT  JTable refreshed
  |
  v
Success or error dialog

MenuBar → File → Export CSV → background fetch → save chooser → CSV file
MenuBar → File → Exit → close application
```

## Stage 3 – Critical design decisions

- Input validation belongs in `RegistrationService`, not only in the UI.
- Duplicate checking is enforced by the database unique constraint on `(student_id, event_name)`. This is race-safe compared with a separate check-then-insert query.
- SQL values are passed through `PreparedStatement`.
- `SwingWorker.doInBackground()` performs database operations; `done()` updates Swing components on the event-dispatch thread.
- The table uses SQL `LIMIT` and `OFFSET` with a page size of 25.
- Credentials are read from environment variables, not stored in source code.

## Stage 4 – Layered class structure

```text
EventRegistrationApp (Swing presentation)
       |
       v
RegistrationService (validation and use cases)
       |
       v
RegistrationRepository (JDBC queries)
       |
       v
Database (connection configuration)
       |
       v
MySQL: registrations table
```

`Registration` is the data model passed from repository to service and UI.

### Register pseudocode

```text
PROCEDURE register
    read student ID, name and event from form
    validate required values and student ID format
    disable Register button
    START SwingWorker
        repository INSERT using PreparedStatement
        database unique constraint rejects duplicate student/event pair
    WHEN worker completes on EDT
        if successful
            show success message
            clear form
            refresh first page
        else
            show validation or duplicate/database error
        enable Register button
END PROCEDURE
```

### Filter pseudocode

```text
PROCEDURE filterRegistrations
    selectedEvent ← filterCombo.getSelectedItem()
    page ← 0
    START SwingWorker
        rows ← repository.findPage(selectedEvent, pageSize, page * pageSize)
    WHEN worker completes on EDT
        clear table model
        add each returned registration to table model
        update status message
END PROCEDURE
```

## Stage 5 – Implementation and test cases

The Swing application, service, repository, model, database connection helper and SQL schema are included.

| Test | Input/action | Expected result |
|---|---|---|
| Valid registration | New student ID, name and event | Row inserted and displayed. |
| Empty input | Leave a required field blank | Clear validation error; no insert. |
| Invalid student ID | ID shorter than 3 or containing unsupported characters | Validation error; no insert. |
| Duplicate registration | Submit same student ID and event twice | Second insert rejected by unique constraint. |
| Filter | Select one event and Apply Filter | Only matching event registrations displayed. |
| Export | File → Export CSV | All matching rows exported to CSV. |
| Responsiveness | Load/filter/export with database configured | UI remains responsive while worker performs JDBC work. |

## Alternative: load all rows vs LIMIT/OFFSET

Loading 100,000 rows at once transfers and materialises every row on initial load. Database and client memory grow with the full result set (O(N × row-size)); table model construction can also block the EDT if not moved to a worker. Startup latency and garbage-collection pressure are higher.

With a page size P, the client initially receives only P rows, so the table's retained data is O(P × row-size). Each page request transfers P rows, and SwingWorker keeps the UI responsive. The database still needs to locate rows for the requested offset; large offsets can become slower, and keyset pagination can be considered for very large datasets. A page-at-a-time design therefore improves initial load time, client memory use and responsiveness.
