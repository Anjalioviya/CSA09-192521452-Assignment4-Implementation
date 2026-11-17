# Task 3 – Campus Library Search Screen

## 3.1 Layout manager decisions

| Region | Layout | Reason |
|---|---|---|
| Main frame | `BorderLayout` | Separates fixed top/bottom regions from the expandable centre. |
| Search row | `FlowLayout(LEFT)` | Keeps field, category and Search button together at preferred sizes. |
| Results region | `BorderLayout.CENTER` with `JScrollPane` | Receives remaining space and grows when the frame is resized. |
| Bottom controls | `BorderLayout` containing `FlowLayout` | Navigation buttons retain preferred sizes; status occupies its own strip. |

## 3.2 Nested container structure

```text
JFrame (BorderLayout)
├── NORTH: title label
├── PAGE_START: search JPanel (BorderLayout)
│   └── CENTER: search row JPanel (FlowLayout)
│       ├── JLabel
│       ├── JTextField
│       ├── JComboBox
│       └── JButton
├── CENTER: JScrollPane → JTable
└── SOUTH: bottom JPanel (BorderLayout)
    ├── CENTER: navigation JPanel (FlowLayout)
    │   ├── Prev JButton
    │   ├── Next JButton
    │   └── Reserve JButton
    └── SOUTH: status JLabel
```

## 3.3 Implementation

Run `LibrarySearchScreen`. The results table is in the centre of the frame, inside a scroll pane. Search and navigation controls are wired to update the status message.

## 3.4 Resizing test plan

Resize the window to a small, medium and large size. The centre table/scroll pane should receive the extra width and height. The search row and navigation controls should remain at their preferred component sizes; at very narrow sizes, FlowLayout may wrap controls.

## Alternative concept and complexity

`GridBagLayout` can express flexible weights and fine-grained constraints but requires more constraint configuration. It can preserve the same resize behaviour when `weightx`, `weighty` and `fill` are set correctly. Absolute positioning with `setLayout(null)` and `setBounds` does not automatically adapt to resizing and requires manual resize logic; components may overlap or become clipped. Layout computation is generally proportional to the number of child components for either manager (roughly O(c) for c components), while absolute positioning has simpler direct bound assignment but shifts complexity into manual resize handling. Nested panels add a small O(p) container overhead for p panels, in exchange for clearer independent layout responsibilities.
