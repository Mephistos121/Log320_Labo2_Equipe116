class Move {

    private int row;
    private int col;
    private int subBoard;

    public Move(int subBoard){
        this.row = -1;
        this.col = -1;
        this.subBoard = subBoard;
    }

    public Move(int r, int c){
        row = r;
        col = c;
    }

    public int getRow(){return row;}
    public int getCol(){return col;}
    public void setRow(int r){row = r;}
    public void setCol(int c){
        col = c;
    }

    @Override
    public String toString() {
        return "Move{" +
                "row=" + row +
                ", col=" + col +
                ", subBoard=" + subBoard  +
                '}';
    }
}
