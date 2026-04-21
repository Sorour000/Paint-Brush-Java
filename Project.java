package com.mycompany.project;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Stack;

public class Project extends JFrame {

    enum Tool { LINE, RECTANGLE, OVAL, FREEHAND, ERASER }

    private Tool selectedTool = Tool.LINE;
    private Color selectedColor = Color.RED;

    private boolean filled = false;
    private boolean dotted = false;

    private DrawPanel drawPanel;

    private JButton undoBtn;

    public Project() {
        setTitle("Paint Brush Project");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);

        add(createToolbar(), BorderLayout.WEST);

        setVisible(true);
    }

    private JPanel createToolbar() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(15, 1, 5, 5));

        JButton red = new JButton("Red");
        red.addActionListener(e -> selectedColor = Color.RED);

        JButton green = new JButton("Green");
        green.addActionListener(e -> selectedColor = Color.GREEN);

        JButton blue = new JButton("Blue");
        blue.addActionListener(e -> selectedColor = Color.BLUE);

        JButton line = new JButton("Line");
        line.addActionListener(e -> selectedTool = Tool.LINE);

        JButton rect = new JButton("Rectangle");
        rect.addActionListener(e -> selectedTool = Tool.RECTANGLE);

        JButton oval = new JButton("Oval");
        oval.addActionListener(e -> selectedTool = Tool.OVAL);

        JButton free = new JButton("Free Hand");
        free.addActionListener(e -> selectedTool = Tool.FREEHAND);

        JButton eraser = new JButton("Eraser");
        eraser.addActionListener(e -> selectedTool = Tool.ERASER);

        JButton clear = new JButton("Clear All");
        clear.addActionListener(e -> drawPanel.clearAll());

        undoBtn = new JButton("Undo");
        undoBtn.addActionListener(e -> drawPanel.undo());

        JCheckBox fillBox = new JCheckBox("Filled");
        fillBox.addActionListener(e -> filled = fillBox.isSelected());

        JCheckBox dottedBox = new JCheckBox("Dotted");
        dottedBox.addActionListener(e -> dotted = dottedBox.isSelected());

        panel.add(red);
        panel.add(green);
        panel.add(blue);

        panel.add(line);
        panel.add(rect);
        panel.add(oval);
        panel.add(free);
        panel.add(eraser);

        panel.add(fillBox);
        panel.add(dottedBox);

        panel.add(undoBtn);
        panel.add(clear);

        return panel;
    }

    class DrawPanel extends JPanel {

        private ArrayList<ShapeObj> shapes = new ArrayList<>();
        private Stack<ShapeObj> undoStack = new Stack<>();

        private int startX, startY, endX, endY;

        public DrawPanel() {
            setBackground(Color.WHITE);

            MouseAdapter ma = new MouseAdapter() {

                public void mousePressed(MouseEvent e) {
                    startX = e.getX();
                    startY = e.getY();

                    if (selectedTool == Tool.FREEHAND || selectedTool == Tool.ERASER) {
                        ShapeObj s = new ShapeObj(startX, startY, startX, startY,
                                selectedTool, selectedTool == Tool.ERASER ? Color.WHITE : selectedColor,
                                false, false);
                        shapes.add(s);
                    }
                }

                public void mouseDragged(MouseEvent e) {
                    endX = e.getX();
                    endY = e.getY();

                    if (selectedTool == Tool.FREEHAND || selectedTool == Tool.ERASER) {
                        ShapeObj s = new ShapeObj(startX, startY, endX, endY,
                                selectedTool, selectedTool == Tool.ERASER ? Color.WHITE : selectedColor,
                                false, false);
                        shapes.add(s);
                        startX = endX;
                        startY = endY;
                    }

                    repaint();
                }

                public void mouseReleased(MouseEvent e) {
                    endX = e.getX();
                    endY = e.getY();

                    if (selectedTool != Tool.FREEHAND && selectedTool != Tool.ERASER) {
                        ShapeObj s = new ShapeObj(startX, startY, endX, endY,
                                selectedTool, selectedColor, filled, dotted);
                        shapes.add(s);
                    }

                    repaint();
                }
            };

            addMouseListener(ma);
            addMouseMotionListener(ma);
        }

        public void clearAll() {
            shapes.clear();
            repaint();
        }

        public void undo() {
            if (!shapes.isEmpty()) {
                undoStack.push(shapes.remove(shapes.size() - 1));
                repaint();
            }
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            for (ShapeObj s : shapes) {
                if (s.dotted) {
                    float[] dash = {10f};
                    g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND, 1f, dash, 0f));
                } else {
                    g2.setStroke(new BasicStroke(2));
                }

                g2.setColor(s.color);

                int x = Math.min(s.x1, s.x2);
                int y = Math.min(s.y1, s.y2);
                int w = Math.abs(s.x1 - s.x2);
                int h = Math.abs(s.y1 - s.y2);

                switch (s.tool) {
                    case LINE:
                        g2.drawLine(s.x1, s.y1, s.x2, s.y2);
                        break;

                    case RECTANGLE:
                        if (s.filled)
                            g2.fillRect(x, y, w, h);
                        else
                            g2.drawRect(x, y, w, h);
                        break;

                    case OVAL:
                        if (s.filled)
                            g2.fillOval(x, y, w, h);
                        else
                            g2.drawOval(x, y, w, h);
                        break;

                    case FREEHAND:
                    case ERASER:
                        g2.drawLine(s.x1, s.y1, s.x2, s.y2);
                        break;
                }
            }
        }
    }

    class ShapeObj {
        int x1, y1, x2, y2;
        Tool tool;
        Color color;
        boolean filled;
        boolean dotted;

        public ShapeObj(int x1, int y1, int x2, int y2,
                        Tool tool, Color color,
                        boolean filled, boolean dotted) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.tool = tool;
            this.color = color;
            this.filled = filled;
            this.dotted = dotted;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Project::new);
    }
}
