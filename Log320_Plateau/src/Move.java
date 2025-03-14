public class Move {

    private int row;
    private int col;

    //IMPORTANT TO KNOW, A MOVE IS ALWAYS RELATIVE TO A FULL BOARD NOT A SUB-BAORD
    public Move(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Move(String move){

        char c0 = move.charAt(2);
        char c1 = move.charAt(1);

        this.row = (((int) c0) - 49);
        this.col = (((int) c1) - 65);
    }

    public int getRow(){return row;}
    public int getCol(){return col;}
    public void setRow(int r){row = r;}
    public void setCol(int c){col = c;}

    @Override
    public String toString() {
        return "Move{" +
                "row=" + (row) +
                ", col=" + col +
                '}';
    }

    public String moveToString() {
        char colChar = (char) (this.col + 65);
        char rowNum = (char)(this.row +49);
        System.out.println("String sent is: "+colChar +rowNum);
        return "" + colChar + rowNum;
    }

}
