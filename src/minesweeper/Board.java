package minesweeper;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import javax.swing.JPanel;

import static java.awt.event.MouseEvent.*;
import static minesweeper.GameState.Mode.Running;

public class Board extends JPanel {
    private GameState state;
    final int cell_size = 50;
    int grid_size_pixel_x;
    int grid_size_pixel_y;
    public Board(GameState state) {
        this.state = state;
        this.grid_size_pixel_x = state.grid_size_x * cell_size;
        this.grid_size_pixel_y = state.grid_size_y * cell_size;

        setPreferredSize(new Dimension(this.grid_size_pixel_x, this.grid_size_pixel_y));

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

        switch (this.state.getMode()) {
            case GameOver:
                drawGameOver(g2d);
                break;
            case Won:
                drawWon(g2d);
                break;
        }
        repaint();
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(Color.black);
        for (int x = 1; x < this.state.grid_size_x; x++) {
            Line2D line = new Line2D.Double(x * this.cell_size, 0, x * this.cell_size, this.grid_size_pixel_y);
            g2d.draw(line);
        }
        for (int y = 1; y < this.state.grid_size_y; y++) {
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
                    if (cell.isFlagged()) {
                        g2d.setColor(Color.red);
                        g2d.fillRect(
                                (int)((x + 0.25) * this.cell_size),
                                (int)((y + 0.25) * this.cell_size),
                                (int)(this.cell_size * 0.5),
                                (int)(this.cell_size * 0.5));
                    }
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

    private void drawWon(Graphics2D g2d) {
        drawTextOverlay(g2d, "You won!");
    }

    private void drawGameOver(Graphics2D g2d) {
        drawTextOverlay(g2d, "Game Over");
    }
    private void drawTextOverlay(Graphics2D g2d, String text) {
        g2d.setStroke(new BasicStroke(1));
        int width = 250;
        int height = 80;
        int x = (int) (grid_size_pixel_x / 2 - width / 2);
        int y = (int) (grid_size_pixel_y / 2 - height / 2);
        g2d.setPaint(Color.gray);
        g2d.fillRect(x, y, width, height);
        g2d.setColor(Color.black);
        g2d.drawRect(x, y, width, height);

        g2d.setColor(Color.black);
        g2d.drawString(text,
            (int)(grid_size_pixel_x / 2 - g2d.getFontMetrics().stringWidth(text) / 2),
         (int)(grid_size_pixel_y / 2 + g2d.getFontMetrics().getHeight() / 2) - 10);
    }

    class MyMouseListener implements MouseListener {
        @Override
        public void mousePressed(MouseEvent e) {
            int cell_x = e.getX() / cell_size;
            int cell_y = e.getY() / cell_size;
            switch (e.getButton()) {
                case BUTTON1:
                    switch (state.getMode()) {
                        case Running:
                            if (!state.board[cell_y][cell_x].isFlagged()) {
                                state.try_uncover(cell_x, cell_y);
                            }
                            break;
                        case GameOver:
                        case Won:
                            // Restart game
                            state.reset();
                    }
                    break;
                case BUTTON3:
                    state.board[cell_y][cell_x].toggleFlagged();
                    break;
            }
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


