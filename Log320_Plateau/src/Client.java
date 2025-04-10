import java.io.*;
import java.net.*;
import java.sql.SQLOutput;
import java.util.*;

class Client {
    public static void main(String[] args) {

        Socket MyClient;
        BufferedInputStream input;
        BufferedOutputStream output;

        //Initialise the board to be empty
        SubBoard[][] subBoardArray = {{new SubBoard(0,0), new SubBoard(0,1), new SubBoard(0,2)}
                , {new SubBoard(1,0), new SubBoard(1,1), new SubBoard(1,2)},
                {new SubBoard(2,0), new SubBoard(2,1), new SubBoard(2,2)}};
        Board gameBoard = new Board(subBoardArray);

        Piece player = Piece.EMPTY;
        Piece other = Piece.EMPTY;
        CPU cpuPlayer = new CPU(player);

        HashMap<Integer,Double> boardStateHashForX = new HashMap<>();
        HashMap<Integer, Double> boardStateHashForO = new HashMap<>();
        BoardStateHash boardStateHasher = new BoardStateHash();

        Board board = new Board(subBoardArray);
        SubBoardGenerator subBoardGenerator = new SubBoardGenerator();
        List<Piece[][]> allPossibleSubBoards = subBoardGenerator.generateAllPossibleBoardStates();

        //Pre evaluate all possible boards for X
        for(int i = 0; i<allPossibleSubBoards.size();i++) {
            SubBoard subBoardEvaluate = new SubBoard(0,0);
            subBoardEvaluate.setSubBoard(allPossibleSubBoards.get(i));
            boardStateHashForX.put(boardStateHasher.boardToIntId(allPossibleSubBoards.get(i)),
                    subBoardEvaluate.evaluate(Piece.X));
        }

        //Pre evaluate all possible boards for O
        for(int i = 0; i<allPossibleSubBoards.size();i++) {
            SubBoard subBoardEvaluate = new SubBoard(0,0);
            subBoardEvaluate.setSubBoard(allPossibleSubBoards.get(i));
            boardStateHashForO.put(boardStateHasher.boardToIntId(allPossibleSubBoards.get(i)),
                    subBoardEvaluate.evaluate(Piece.O));
        }


        System.out.println("Here is the size of the  all possible SubBoards: "+allPossibleSubBoards.size());
        System.out.println("Here is the size of the x hashmap: "+boardStateHashForX.size());
        System.out.println("Here is the size of the o hashmap: "+boardStateHashForO.size());

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

                    other = Piece.O;
                    player = Piece.X;

                    cpuPlayer = new CPU(player);
                    cpuPlayer.setBoardHashForO(boardStateHashForO);
                    cpuPlayer.setBoardHashForX(boardStateHashForX);

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

                    other = Piece.X;
                    player = Piece.O;

                    cpuPlayer = new CPU(player);
                    cpuPlayer.setBoardHashForO(boardStateHashForO);
                    cpuPlayer.setBoardHashForX(boardStateHashForX);
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
                    ArrayList<Move> alphaBeta = cpuPlayer.getNextMoveMinMaxAlphaBeta(gameBoard,enmemyMove);
                    //Move ourMove = moves.get(getRandomIndex(moves));
                    Move ourMove = alphaBeta.get(0);
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
}