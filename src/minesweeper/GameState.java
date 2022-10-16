package minesweeper;

import java.util.*;

import static java.lang.Math.*;

class Cell {
    private int number;
    private boolean bomb;
    private boolean covered;
    private boolean flagged = false;

    Cell(int number, boolean bomb, boolean covered) {
        this.number = number;
        this.bomb = bomb;
        this.covered = covered;
    }

    int getNumber() {
        return this.number;
    }

    boolean isBomb() {
        return this.bomb;
    }

    boolean isCovered() {
        return this.covered;
    }

    void uncover() {
        this.covered = false;
    }

    boolean isFlagged() {
        return this.flagged;
    }
    void toggleFlagged() {
        this.flagged = !this.flagged;
    }
}

class NumberCell extends Cell {
    NumberCell(int number, boolean isCovered) {
        super(number, false, isCovered);
    }
}

class BombCell extends Cell {
    BombCell() {
        super(0, true, true);
    }
}

class Coord {
    public int x, y;
    Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

public class GameState {
    public Cell[][] board;
    private int n_covered;
    private int n_bombs;
    int grid_size_x;
    int grid_size_y;
    enum Mode {
        Uninitialized,
        Running,
        Won,
        GameOver
    }
    // It would make more sense to call this 'game_state' or something similar, but that's already
    // the name of the class, so I think that would be even more confusing
    private Mode mode = Mode.Uninitialized;

    GameState(int size_x, int size_y, int n_bombs) {
        this.grid_size_x = size_x;
        this.grid_size_y = size_y;
        this.n_covered = size_x * size_y;
        this.n_bombs = n_bombs;
        generateBoard(n_bombs);
        mode = Mode.Running;
    }

    private void generateBoard(int n_bombs) {
        assert n_bombs <= this.grid_size_x * this.grid_size_y;

        Random rand = new Random();
        int[][] bombs = new int[this.grid_size_y][this.grid_size_x];
        for (int i = 0; i < n_bombs; i++) {
            while (true) {
                int x = rand.nextInt(this.grid_size_x);
                int y = rand.nextInt(this.grid_size_y);
                if (bombs[y][x] == 0) {
                    bombs[y][x] = 1;
                    break;
                }
            }
        }

        board = new Cell[this.grid_size_y][this.grid_size_x];
        for (int i = 0; i < this.grid_size_x; i++) {
            for (int j = 0; j < this.grid_size_y; j++) {
                if (bombs[j][i] > 0) {
                    board[j][i] = new BombCell();
                } else {
                    // Calculate number of bombs surrounding the cell
                    int n_surrounding_bombs = 0;
                    for (int k = max(i - 1, 0); k <= min(i + 1, this.grid_size_x - 1); k++) {
                        for (int l = max(j - 1, 0); l <= min(j + 1, this.grid_size_y - 1); l++) {
                            n_surrounding_bombs += bombs[l][k];
                        }
                    }
                    board[j][i] = new NumberCell(n_surrounding_bombs, true);
                }
            }
        }
    }

    public boolean try_uncover(int x, int y) {
        if (this.board[y][x].isBomb()) {
            this.mode = Mode.GameOver;
            System.out.println("bomb!");
            return false;
        } else {
            uncover_adjacent_empty_cells(x, y);
            uncover(x, y);
            if (this.n_covered == this.n_bombs) {
                this.mode = Mode.Won;
                System.out.println("You won!");
            }
            return true;
        }
    }

    private void uncover(int x, int y) {
        if (this.board[y][x].isCovered()) {
            this.n_covered -= 1;
        }
        this.board[y][x].uncover();
    }

    private void uncover_adjacent_empty_cells(int x, int y) {
        LinkedList<Coord> queue = new LinkedList<>();
        queue.add(new Coord(x, y));
        boolean[][] visited = new boolean[grid_size_y][grid_size_x];
        Coord next;
        Cell next_cell;
        while ((next = queue.poll()) != null) {
            visited[next.y][next.x] = true;
            uncover(next.x, next.y);
            next_cell = this.board[next.y][next.x];
            if (next_cell.getNumber() == 0) {
                int new_x, new_y;
                // Iterate over surrounding cells
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        new_x = next.x + i;
                        new_y = next.y + j;
                        if ( 0 <= new_x && new_x < this.grid_size_x
                        && 0 <= new_y && new_y < this.grid_size_y ) {
                            if (i == 0 && j == 0) {
                                continue;
                            }
                            if (!visited[new_y][new_x]) {
                                queue.add(new Coord(new_x, new_y));
                            }
                        }
                    }
                }
            }
        }
    }
}
