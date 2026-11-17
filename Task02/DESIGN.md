# Task 2 – Hospital Outpatient Appointment Desk

## 2.1 Event model

| Event source | Event object | Listener interface | Action |
|---|---|---|---|
| Department combo box | `ItemEvent` / `ActionEvent` | `ItemListener` / `ActionListener` | Refresh doctor choices for selected department. |
| Patient ID text field (Enter) | `ActionEvent` | `ActionListener` | Validate or move focus to department. |
| Doctor combo box | `ActionEvent` | `ActionListener` | Retain selected doctor and validate availability. |
| Time-slot combo box | `ActionEvent` | `ActionListener` | Retain selected slot. |
| Book button | `ActionEvent` | `ActionListener` | Validate all fields and submit booking. |
| Window close button | `WindowEvent` | `WindowListener` / `WindowAdapter` | If booking is in progress, ask before closing; otherwise dispose. |

## 2.2 Component hierarchy

```text
JFrame: AppointmentDesk
└── JPanel (BorderLayout)
    ├── NORTH: JLabel – "Outpatient Appointment Desk"
    ├── CENTER: JPanel (GridLayout, 4 rows × 2 columns)
    │   ├── JLabel + JTextField (patient ID)
    │   ├── JLabel + JComboBox (department)
    │   ├── JLabel + JComboBox (doctor)
    │   └── JLabel + JComboBox (time slot)
    └── SOUTH: JPanel (FlowLayout)
        ├── JButton (Book Appointment)
        └── JLabel (status / confirmation / error)
```

## 2.3 Handler pseudocode

### (a) Department change

```text
PROCEDURE refreshDoctors
    selectedDepartment ← departmentCombo.getSelectedItem()
    doctorCombo.removeAllItems()
    doctorList ← doctorMap.get(selectedDepartment)
    FOR EACH doctor IN doctorList
        doctorCombo.addItem(doctor)
    END FOR
    IF doctorList is empty THEN
        disable bookButton
    ELSE
        enable bookButton
    END IF
END PROCEDURE
```

### (b) Validate and book

```text
PROCEDURE bookAppointment
    patientId ← trim(patientIdField.getText())
    department ← departmentCombo.getSelectedItem()
    doctor ← doctorCombo.getSelectedItem()
    slot ← slotCombo.getSelectedItem()

    IF patientId is empty THEN
        show error "Enter patient ID"
        RETURN
    END IF
    IF department, doctor or slot is not selected THEN
        show error "Complete all selections"
        RETURN
    END IF

    set bookingInProgress ← TRUE
    disable bookButton
    TRY
        booking ← appointmentService.book(patientId, department, doctor, slot)
        show confirmation with booking details
    CATCH booking error
        show error message
    FINALLY
        set bookingInProgress ← FALSE
        enable bookButton
    END TRY
END PROCEDURE
```

## 2.4 Event sequence and applet comparison

1. `main` schedules GUI creation on the Swing event-dispatch thread with `SwingUtilities.invokeLater`.
2. The frame constructor creates components and registers listeners.
3. `setVisible(true)` displays the frame.
4. A department selection generates an item/action event; its listener refreshes the doctor combo box.
5. Book generates an action event; the handler validates and calls the service.
6. Closing generates a `WindowEvent`; `windowClosing` asks for confirmation if a booking is in progress, then disposes only when confirmed.

An applet uses container-managed lifecycle callbacks: `init` initializes once, `start` begins/resumes, `paint` renders, `stop` pauses and `destroy` releases resources. A desktop `JFrame` does not receive those applet lifecycle callbacks; it is created by application code and closed through window events.

## Alternative concept and complexity

With a preloaded `Map<String, List<Doctor>>`, department changes use a map lookup and iterate only the selected department's doctor list: expected O(1) lookup plus O(m_d) to populate the combo box. Memory is O(d + m), where `d` is departments and `m` is all doctors. Fetching from a database on every change adds network/query latency to every selection and may make the UI unresponsive unless asynchronous. It reduces cached doctor data in memory, but increases database work and repeated transfer. A background worker is required for database fetching.
