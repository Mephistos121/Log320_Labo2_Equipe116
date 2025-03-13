public class Move {

    private int row;
    private int col;

    //IMPORTANT TO KNOW, A MOVE IS ALWAYS RELATIVE TO A FULL BOARD NOT A SUB-BAORD
    public Move(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow(){return row;}
    public int getCol(){return col;}
    public void setRow(int r){row = r;}
    public void setCol(int c){col = c;}

    @Override
    public String toString() {
        return "Move{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }

}
