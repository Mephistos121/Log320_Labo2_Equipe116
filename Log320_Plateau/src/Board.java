public class Board {
    private SubBoard[][] boards;

    public Board(SubBoard[][] boards) {
        this.boards = boards;
    }

    public SubBoard[][] getBoards() {
        return boards;
    }

    public void setBoards(SubBoard[][] boards) {
        this.boards = boards;
    }
    public void setSubBoards(int row, int col,SubBoard subBoard) {
        this.boards[row][col] = subBoard;
    }
    public SubBoard getSubBoard(int row, int col) {
        return boards[row][col];
    }
}
