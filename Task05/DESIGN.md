# Task 5 – Same Screen, Different Event-Handling Styles

## 5.1 Four implementations

Four independent classes are supplied:

1. `FrameListenerConverter` – the frame implements `ActionListener`.
2. `InnerClassConverter` – a named inner `ConvertHandler` implements `ActionListener`.
3. `AnonymousClassConverter` – an anonymous `ActionListener` is registered.
4. `Converter` – one lambda listener is registered with both the Convert button and amount field.

All four also register a `WindowAdapter`, respond to Enter in the amount field, validate numeric input, and show the conversion in a dialog.

## 5.2 Requirement change

The Enter key is implemented by registering the same handler on the `JTextField` as on the button. The dialog is shown from the shared conversion method/handler. The lambda version needs only one listener variable and two registrations; the other versions also reuse one handler instance.

## 5.3 Comparison

| Style | Readability | Access to frame fields | Reuse | Ease of change |
|---|---|---|---|---|
| Frame implements listener | Simple for tiny windows, but mixes UI and handler | Direct | Frame itself is coupled to the action | Can make a large frame crowded |
| Named inner class | Clear named responsibility | Direct | Handler instance can be reused | Good for larger handler logic |
| Anonymous inner class | Local to registration point | Direct | Reuse only if stored in a variable | More verbose |
| Lambda | Concise for a single-method interface | Lexically available | Store one listener and register on several sources | Easiest for short handlers |

For a large application, I would use a named handler or a shared Swing `Action` where the behaviour is substantial and reused across buttons, menus and keyboard shortcuts. Lambdas are suitable for short event actions.

## Alternative concept and complexity

A method reference (`this::convert`) can replace a lambda when the method signature matches the listener callback. A single `Action` object can be shared by a button, menu item and keyboard shortcut, centralising enabled state, name and action behaviour. A separate listener per trigger may create more objects; sharing one action reduces duplicated handler instances. Adding a new trigger with a shared action requires only registering that action with the new component, while duplicated handlers require another registration and potentially duplicated code.
