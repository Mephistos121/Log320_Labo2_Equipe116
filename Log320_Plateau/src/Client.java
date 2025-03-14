import java.io.*;
import java.net.*;

class Client {
    public static void main(String[] args) {

        Socket MyClient;
        BufferedInputStream input;
        BufferedOutputStream output;

        //Initialise the board to be empty
        SubBoard[][] subBoard = {{new SubBoard(0,0), new SubBoard(0,1), new SubBoard(0,2)}
                , {new SubBoard(1,0), new SubBoard(1,1), new SubBoard(1,2)},
                {new SubBoard(2,0), new SubBoard(2,1), new SubBoard(2,2)}};
        Board gameBoard = new Board(subBoard);

        Piece player = Piece.EMPTY;
        Piece other = Piece.EMPTY;
        Move prevMove = null;

        displayBoard(gameBoard);


        try {
            MyClient = new Socket("localhost", 8888);
            input    = new BufferedInputStream(MyClient.getInputStream());
            output   = new BufferedOutputStream(MyClient.getOutputStream());
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            while(1 == 1){
                char cmd = 0;

                cmd = (char)input.read();
                //System.out.println(cmd);

                // Debut de la partie en joueur blanc
                if(cmd == '1'){
                    byte[] aBuffer = new byte[1024];

                    other = Piece.O;
                    player = Piece.X;

                    System.out.println("Nouvelle partie! Vous jouer blanc, entrez votre premier coup : ");
                    Move move = new Move("E4");

                    prevMove = move;
                    addMoveToBoard(move, player, gameBoard);

                    output.write(move.toString().getBytes(),0,2);
                    output.flush();
                }

                // Debut de la partie en joueur Noir
                if(cmd == '2'){
                    System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des blancs");
                    byte[] aBuffer = new byte[1024];

                    other = Piece.X;
                    player = Piece.O;

                   //TODO FILL UP BOARD WITH SENT INFO

                }
                // Le serveur demande le prochain coup
                // Le message contient aussi le dernier coup joue.
                if(cmd == '3'){
                    byte[] aBuffer = new byte[16];

                    int size = input.available();
                    System.out.println("size :" + size);
                    input.read(aBuffer,0,size);

                    String s = new String(aBuffer);
                    System.out.println("This is the string received: "+s);

                    Move enmemyMove = new Move(s);
                    System.out.println("Move received :"+ enmemyMove);
                    addMoveToBoard(enmemyMove,other,gameBoard);

                    System.out.println("Entrez votre coup : ");

                    Move ourMove = new Move(" E6");
                    addMoveToBoard(ourMove, player, gameBoard);
                    displayBoard(gameBoard);
                    output.write(ourMove.moveToString().getBytes(),0,2);
                    output.flush();
                }

                // Le dernier coup est invalide
                if(cmd == '4'){

                    System.out.println("Coup invalide, entrez un nouveau coup : ");

                    //Temporary move for connection to compile
                    Move ourMove = new Move(" E2");
                    addMoveToBoard(ourMove, player, gameBoard);
                    displayBoard(gameBoard);
                    output.write(ourMove.moveToString().getBytes(),0,2);
                    output.flush();
                    break;
                }

                // La partie est terminée
                if(cmd == '5'){
                    byte[] aBuffer = new byte[16];
                    int size = input.available();
                    input.read(aBuffer,0,size);
                    String s = new String(aBuffer);
                    System.out.println("Partie Terminé. Le dernier coup joué est: "+s);
                    break;
                }
            }
        }
        catch (IOException e) {
            System.out.println("");
            System.out.println("bad connection");
        }

    }

    public static void addMoveToBoard(Move move, Piece piece, Board board) {
        board.getSubBoard(move.getRow()/3, move.getCol()/3).play(move, piece);
        displayBoard(board);
    }

    public static void displayBoard(Board board) {
        final String COLOR_DEFAULT = "\u001B[0m";
        final String COLOR_RED = "\u001B[31m";
        final String COLOR_BLUE = "\u001B[34m";
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-");

        int offSetX;
        int offSetY = 0;
        for(int i = 0; i < 9; i++) {
            offSetX = 0;
            if(i != 0 && i%3 == 0) {
                System.out.println("\n---------┼---------┼---------");
                offSetY++;
            }
            else
                System.out.println();
            for(int j = 0; j < 9; j++) {
                if (j != 0 && j % 3 == 0) {
                    System.out.print("|");
                    offSetX++;
                }
                //System.out.println(offSetX+", "+offSetY);

                Piece p = board.getSubBoard(i/3,j/3).getValueAt(i%3, j%3);
                if(p == Piece.X)
                    System.out.print(COLOR_RED);
                else if(p == Piece.O)
                    System.out.print(COLOR_BLUE);
                System.out.print(" "+ (p==Piece.EMPTY?" ":p)+" ");
                System.out.print(COLOR_DEFAULT);
            }
        }
    }
}