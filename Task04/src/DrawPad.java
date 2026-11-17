import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/** Stores click points and redraws them during every paint operation. */
public class DrawPad extends JFrame {
    private final List<Point> points = new ArrayList<>();
    private final JLabel status = new JLabel("Clicks: 0");
    private final DrawingArea drawingArea = new DrawingArea();

    public DrawPad() {
        super("Draw Pad");
        setLayout(new BorderLayout());
        add(drawingArea, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        drawingArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                points.add(e.getPoint());
                status.setText("Clicks: " + points.size());
                drawingArea.repaint();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(500, 360);
        setLocationRelativeTo(null);
    }

    private class DrawingArea extends Canvas {
        DrawingArea() {
            setBackground(Color.WHITE);
        }

        @Override public Dimension getPreferredSize() {
            return new Dimension(480, 300);
        }

        @Override public void paint(Graphics g) {
            super.paint(g);
            g.setColor(Color.BLUE);
            for (Point point : points) {
                g.fillOval(point.x - 3, point.y - 3, 6, 6);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DrawPad().setVisible(true));
    }
}
