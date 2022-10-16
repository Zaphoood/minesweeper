package minesweeper;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import javax.swing.JPanel;

public class Board extends JPanel {
    private GameState state;
    final int cell_size = 50;
    int grid_size_pixel_x;
    int grid_size_pixel_y;
    public Board(GameState state) {
        this.state = state;
        this.grid_size_pixel_x = state.grid_size_x * cell_size;
        this.grid_size_pixel_y = state.grid_size_y * cell_size;

        setPreferredSize(new Dimension(500, 500));

        addMouseListener(new MyMouseListener());
        setFocusable(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);
        g.setFont(g.getFont().deriveFont(30f));

        drawCells(g2d);
        drawGrid(g2d);

        repaint();
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(Color.black);
        for (int x = 1; x < this.state.grid_size_x; x++) {
            Line2D line = new Line2D.Double(x * this.cell_size, 0, x * this.cell_size, this.grid_size_pixel_y);
            g2d.draw(line);
        }
        for (int y = 1; y < this.state.grid_size_x; y++) {
            Line2D line = new Line2D.Double(0, y * this.cell_size, this.grid_size_pixel_x, y * this.cell_size);
            g2d.draw(line);
        }
    }

    private void drawCells(Graphics2D g2d) {
        for (int x = 0; x < this.state.grid_size_x; x++) {
            for (int y = 0; y < this.state.grid_size_y; y++) {
                Cell cell = this.state.board[y][x];
                if (cell.isCovered()) {
                    g2d.setColor(Color.gray);
                    g2d.fillRect(x * this.cell_size, y * this.cell_size, this.cell_size, this.cell_size);
                } else if (cell.getNumber() > 0) {
                    // Draw number
                    g2d.setColor(Color.black);
                    String num_as_str = Integer.toString(cell.getNumber());
                    g2d.drawString(num_as_str,
                        (int)((x + 0.5) * cell_size - g2d.getFontMetrics().stringWidth(num_as_str) / 2),
                        (int)((y + 0.5) * cell_size + g2d.getFontMetrics().getHeight() / 2)
                    );
                }
            }
        }
    }

    class MyMouseListener implements MouseListener {
        @Override
        public void mousePressed(MouseEvent e) {
            int cell_x = e.getX() / cell_size;
            int cell_y = e.getY() / cell_size;
            state.try_uncover(cell_x, cell_y);
        }

        @Override
        public void mouseClicked(MouseEvent e) { }

        @Override
        public void mouseReleased(MouseEvent e) { }

        @Override
        public void mouseEntered(MouseEvent e) { }

        @Override
        public void mouseExited(MouseEvent e) { }
    }

}


