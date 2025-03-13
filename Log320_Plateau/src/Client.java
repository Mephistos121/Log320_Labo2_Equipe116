import java.io.*;
import java.net.*;

class Client {
    public static void main(String[] args) {

        Socket MyClient;
        BufferedInputStream input;
        BufferedOutputStream output;
        int[][] board = new int[9][9];

        //what am I doing???
        SubBoard[][] subBoard = {{new SubBoard(0,0), new SubBoard(0,1), new SubBoard(0,2)}
                , {new SubBoard(1,0), new SubBoard(1,1), new SubBoard(1,2)},
                {new SubBoard(2,0), new SubBoard(2,1), new SubBoard(2,2)}};

        Piece player = Piece.EMPTY;
        Piece other = Piece.EMPTY;
        String prevMove = "";
        displayBoard(subBoard);

        try {
            MyClient = new Socket("localhost", 8888);

            input    = new BufferedInputStream(MyClient.getInputStream());
            output   = new BufferedOutputStream(MyClient.getOutputStream());
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            while(1 == 1){
                char cmd = 0;

                cmd = (char)input.read();
                System.out.println(cmd);
                // Debut de la partie en joueur blanc
                if(cmd == '1'){
                    byte[] aBuffer = new byte[1024];

                    other = Piece.O;
                    player = Piece.X;

                    int size = input.available();
                    //System.out.println("size " + size);
                    input.read(aBuffer,0,size);
                    String s = new String(aBuffer).trim();
                    System.out.println(s);
                    String[] boardValues;
                    boardValues = s.split(" ");
                    int x=0,y=0;
                    for(int i=0; i<boardValues.length;i++){
                        board[x][y] = Integer.parseInt(boardValues[i]);
                        x++;
                        if(x == 9){
                            x = 0;
                            y++;
                        }
                    }

                    System.out.println("Nouvelle partie! Vous jouer blanc, entrez votre premier coup : ");
                    String move = null;
                    move = console.readLine();

                    prevMove = move;
                    addMoveToBoard(move, player, subBoard);

                    output.write(move.getBytes(),0,move.length());
                    output.flush();
                }
                // Debut de la partie en joueur Noir
                if(cmd == '2'){
                    System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des blancs");
                    byte[] aBuffer = new byte[1024];

                    other = Piece.X;
                    player = Piece.O;

                    int size = input.available();
                    //System.out.println("size " + size);
                    input.read(aBuffer,0,size);
                    String s = new String(aBuffer).trim();

                    System.out.println(s);

                    addMoveToBoard(s.trim(),other, subBoard);

                    String[] boardValues;
                    boardValues = s.split(" ");
                    int x=0,y=0;
                    for(int i=0; i<boardValues.length;i++){
                        board[x][y] = Integer.parseInt(boardValues[i]);
                        x++;
                        if(x == 9){
                            x = 0;
                            y++;
                        }
                    }
                }


                // Le serveur demande le prochain coup
                // Le message contient aussi le dernier coup joue.
                if(cmd == '3'){
                    byte[] aBuffer = new byte[16];

                    int size = input.available();
                    System.out.println("size :" + size);
                    input.read(aBuffer,0,size);

                    String s = new String(aBuffer);
                    System.out.println("Dernier coup :"+ s);
                    System.out.println("Entrez votre coup : ");

                    addMoveToBoard(s.trim(),other,subBoard);

                    String move = null;
                    move = console.readLine();

                    addMoveToBoard(move, player, subBoard);
                    prevMove = move;

                    output.write(move.getBytes(),0,move.length());
                    output.flush();

                }
                // Le dernier coup est invalide
                if(cmd == '4'){

                    System.out.println("Coup invalide, entrez un nouveau coup : ");
                    String move = null;
                    move = console.readLine();

                    addMoveToBoard(prevMove, Piece.EMPTY, subBoard);
                    addMoveToBoard(move, player, subBoard);
                    prevMove = move;

                    output.write(move.getBytes(),0,move.length());
                    output.flush();

                }

                // La partie est terminée
                if(cmd == '5'){
                    byte[] aBuffer = new byte[16];
                    int size = input.available();
                    input.read(aBuffer,0,size);
                    String s = new String(aBuffer);
                    System.out.println("Partie Terminé. Le dernier coup joué est: "+s);
                    String move = null;
                    move = console.readLine();
                    output.write(move.getBytes(),0,move.length());
                    output.flush();

                }
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }

    }

    public static void addMoveToBoard(String move, Piece piece, SubBoard[][] board) {
        //on oublie pas de substract le ascii
        char c0 = move.charAt(0);
        char c1 = move.charAt(1);
        int row = (((int)c1)-49);
        int col = (((int)c0)-65);
        int bX, bY;
        if('A' <= c0 && c0 <= 'C')
            bX = 0;
        else if('D' <= c0 && c0 <= 'F')
            bX = 1;
        else
            bX = 2;

        if('1' <= c1 && c1 <= '3')
            bY = 2;
        else if('4' <= c1 && c1 <='6')
            bY = 1;
        else
            bY = 0;
        System.out.println(row+", "+col);
        board[bX][bY].setValueAt((8-row)%3, (col)%3, piece);
        displayBoard(board);
    }

    public static void displayBoard(SubBoard[][] board) {
        final String COLOR_DEFAULT = "\u001B[0m";
        final String COLOR_RED = "\u001B[31m";
        final String COLOR_BLUE = "\u001B[34m";

        int offSetX;
        int offSetY = 0;
        for(int i = 0; i < board.length*board.length; i++) {
            offSetX = 0;
            if(i != 0 && i%3 == 0) {
                System.out.println("\n---------┼---------┼---------");
                offSetY++;
            }
            else
                System.out.println();
            for(int j = 0; j < board.length* board.length; j++) {
                if (j != 0 && j % 3 == 0) {
                    System.out.print("|");
                    offSetX++;
                }
                //System.out.println(offSetX+", "+offSetY);
                Piece p = board[offSetX][offSetY].getValueAt(i%3, j%3);

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