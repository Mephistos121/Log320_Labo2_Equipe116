import java.io.*;
import java.net.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        CPU cpuPlayer = new CPU(player);

        try {
            MyClient = new Socket("localhost", 8888);
            input    = new BufferedInputStream(MyClient.getInputStream());
            output   = new BufferedOutputStream(MyClient.getOutputStream());
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            Move ourLastMove = null;
            while(1 == 1){
                char cmd = 0;

                cmd = (char)input.read();
                //System.out.println(cmd);

                // Debut de la partie en joueur rouge
                if(cmd == '1'){
                    byte[] aBuffer = new byte[1024];
                    int size = input.available();
                    input.read(aBuffer,0,size);
                    String s = new String(aBuffer).trim();
                    System.out.println(s);
                    gameBoard = new Board(createBoardFromString(s));


                    other = Piece.O;
                    player = Piece.X;

                    cpuPlayer = new CPU(player);

                    System.out.println("Nouvelle partie! Vous jouer rouge, entrez votre premier coup : ");
                    Move ourMove = new Move(".E5");
                    ourLastMove = ourMove;
                    addMoveToBoard(ourMove, player, gameBoard);

                    output.write(ourMove.moveToString().getBytes(),0,2);
                    output.flush();
                }

                // Debut de la partie en joueur Noir
                if(cmd == '2'){
                    System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des blancs");
                    byte[] aBuffer = new byte[1024];
                    int size = input.available();
                    input.read(aBuffer,0,size);
                    String s = new String(aBuffer).trim();
                    System.out.println(s);
                    gameBoard = new Board(createBoardFromString(s));

                    other = Piece.X;
                    player = Piece.O;
                    cpuPlayer = new CPU(player);
                    //Move move = new Move(".E4");
                    //addMoveToBoard(move, player, gameBoard);
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
                    // check if enemy move is valid here
                    checkEnemyMove(gameBoard,ourLastMove,enmemyMove);
                    addMoveToBoard(enmemyMove,other,gameBoard);
                    
                    System.out.println("Entrez votre coup : ");
                    ArrayList<Move> alphaBeta = cpuPlayer.getNextMoveMinMaxAlphaBeta(13, gameBoard,enmemyMove);
                    //Move ourMove = moves.get(getRandomIndex(moves));
                    Move ourMove = alphaBeta.get(0);
                    //Move ourMove = new Move(" E6");
                    addMoveToBoard(ourMove, player, gameBoard);
                    ourLastMove = ourMove;
                    output.write(ourMove.moveToString().getBytes(),0,2);
                    output.flush();
                }

                // Le dernier coup est invalide
                if(cmd == '4'){

                    System.out.println("Coup invalide, entrez un nouveau coup : ");
                    System.out.println("We fucked up");
                    //Temporary move for connection to compile
                    //Move ourMove = new Move(" E2");
                    //addMoveToBoard(ourMove, player, gameBoard);
                    //output.write(ourMove.moveToString().getBytes(),0,2);
                    //output.flush();
                    break;
                }

                // La partie est terminée
                if(cmd == '5'){
                    byte[] aBuffer = new byte[16];
                    int size = input.available();
                    input.read(aBuffer,0,size);
                    String s = new String(aBuffer);
                    System.out.println("Partie Terminé. Le dernier coup joué est: "+s);
                }
            }
        }
        catch (IOException e) {
            System.out.println("");
            System.out.println("bad connection");
        }

    }

    private static void checkEnemyMove(Board gameBoard, Move ourLastMove, Move enmemyMove) {
        if(ourLastMove == null) return;
        ArrayList<Move> moves = gameBoard.getValidMoves(ourLastMove);
        for (Move move : moves) {
            if (move.toString().equals((enmemyMove).toString())){
                return;
            }
        }
        throw new RuntimeException("Last enemy move was invalid");
    }

    public static void addMoveToBoard(Move move, Piece piece, Board board) {
        board.getSubBoard(move.getRow()/3, move.getCol()/3).play(move, piece);
        board.displayBoard();
    }

    public static SubBoard determineSubBoard(Move move, Board board) {
        return board.getSubBoard(move.getRow()%3, move.getCol()%3);
    }


    public static int getRandomIndex(ArrayList<?> list) {
        // Check if the list is empty
        if (list.isEmpty()) {
            return -1; // Or throw an exception, or handle it as you prefer
        }

        // Create a Random object
        Random random = new Random();

        // Generate a random index within the bounds of the ArrayList
        int randomIndex = random.nextInt(list.size());

        return randomIndex;
    }

    public static SubBoard[][] createBoardFromString(String s) {
        Pattern pa = Pattern.compile("[^024]");
        Matcher m = pa.matcher(s);
        s = m.replaceAll("");
        char[] t = s.toCharArray();

        SubBoard[][] subBoard = {{new SubBoard(0, 0), new SubBoard(0, 1), new SubBoard(0, 2)}
                , {new SubBoard(1, 0), new SubBoard(1, 1), new SubBoard(1, 2)},
                {new SubBoard(2, 0), new SubBoard(2, 1), new SubBoard(2, 2)}};
        int i = 0;
        int j = 0;
        int x = 0;
        int y = 0;
        int idx = 0;
        Piece p;


        while(i != 2 || j != 2) {
            while(x != 2 || y != 2) {
                idx = (j*3+i*27+y*9+x);

                if(t[idx] == '2')
                    p = Piece.O;
                else if(t[idx] == '4')
                    p = Piece.X;
                else
                    p = Piece.EMPTY;
                //invert vertically
                subBoard[2-i][j].setValueAt(2-y, x, p);

                x++;
                if(x != 0 && x%3 == 0) {
                    y++;
                    x = 0;
                }
            }
            if(j == 2) {
                j = 0;
                i++;
            }
            else
                j++;
            x = 0;
            y = 0;

        }


        Board b = new Board(subBoard);
        b.displayBoard();
        return subBoard;
    }
}