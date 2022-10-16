package minesweeper;

import java.awt.EventQueue;

public class Application {
    static GameState state;
    static Game game;

    public static void main(String[] args) {
        int grid_size_x = 10;
        int grid_size_y = grid_size_x;
        int n_bombs = 10;

        state = new GameState(grid_size_x, grid_size_y, n_bombs);
        EventQueue.invokeLater(() -> {
            game = new Game(state);
            game.setVisible(true);
        });
    }
}