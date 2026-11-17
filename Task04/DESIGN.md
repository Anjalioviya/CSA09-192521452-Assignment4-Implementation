# Task 4 – Event-Handling Forensics

## 4.1 Defect

The method is written as `mouseclicked`, but the listener method name is case-sensitive and must be `mouseClicked(MouseEvent e)`. Because it does not override the interface method, the callback is never invoked. The label also never changes because the supplied code does not call `status.setText(...)`.

## 4.2 Failing interaction

Click to draw a dot, then minimise and restore the window or cover it with another window and uncover it. The dot disappears because `getGraphics()` draws directly to the current screen surface. That drawing is not retained as component state and is lost when the operating system requests a repaint.

## 4.3 Repair

The completed program stores each click as a `Point`, updates the label, and calls `repaint()`. The drawing component iterates over the stored points in `paint(Graphics)`, drawing a 6 × 6 circle centred at each point. Repainting therefore reconstructs all dots.

## 4.4 Prevent recurrence

Adding `@Override` above the misspelled `mouseclicked` method causes a compile-time error similar to: `method does not override or implement a method from a supertype`. The compiler catches the spelling/capitalisation defect before execution.

## Alternative concept and complexity

An off-screen `BufferedImage` can retain the drawing. Each click updates the image once; `paint` then copies the image to the screen. The point-list design uses O(n) memory for n points and O(n) drawing work per repaint. The image design uses O(width × height) pixel storage and usually O(width × height) copy work per repaint, independent of the number of clicks. The image can make repeated repainting faster for many marks, but uses more fixed memory and requires image initialisation/resizing logic.
