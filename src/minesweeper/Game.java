package minesweeper;

import javax.swing.JFrame;

class Game extends JFrame {

    private GameState state;

    public Game(GameState state) {
        this.state = state;

        initUI();
    }

    private void initUI() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(new Board(this.state));
        pack();
    }

}
