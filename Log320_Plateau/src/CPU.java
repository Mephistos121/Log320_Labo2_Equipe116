import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CPU {
    Piece cpuMark;
    final int WORSE_SCORE = Integer.MIN_VALUE;
    final int BEST_SCORE = Integer.MAX_VALUE;

    //Basic evaluate points sett here for easy changing
    private static final int OVERALL_WIN_SCORE = 100000;
    private static final int SUB_BOARD_WIN_SCORE = 100;



    public CPU(Piece cpuMark) {
        this.cpuMark = cpuMark;
    }

    public ArrayList<Move> getNextMoveMinMax(int maxDepth, Board board, Move lastMove, Piece playedPiece) {
        ArrayList<Move> moves = new ArrayList<>();
        int max = WORSE_SCORE;
        ArrayList<Move> possibleMoves = board.getValidMoves(lastMove);



        for (Move move : possibleMoves) {
            board.playMove(move, playedPiece== Piece.O ? Piece.X : Piece.O);
            int ret = minMax( cpuMark == Piece.O ? Piece.X : Piece.O, maxDepth-1, board, move,false);
            board.undoMove(move);



            if (ret == max) {
                moves.add(move);
            } else if(ret > max) {
                moves.clear();
                moves.add(move);
                max = ret;
            }
        }


        //System.out.println(max);
        return moves;
    }

    private int minMax(Piece player, int depth, Board board, Move playedMove, Boolean isMaxing) {
        int currentScore = evaluateBoard(board, player, playedMove);
        if(depth <= 0 || board.isDone()!=Piece.EMPTY){
            return currentScore;
        }
        ArrayList<Move> possibleMoves = board.getValidMoves(playedMove);
        if(isMaxing){
            int maxScore = WORSE_SCORE;
            for (Move move : possibleMoves) {
                board.playMove(move, player == Piece.O ? Piece.X : Piece.O);
                int retScore = minMax(player == Piece.O ? Piece.X : Piece.O, depth - 1, board, move,false);
                board.undoMove(move);
                if (retScore >= maxScore) {
                    maxScore = retScore;
                }
            }
            return maxScore;
        }else{
            int maxScore = BEST_SCORE;
            for (Move move : possibleMoves) {
                board.playMove(move, player == Piece.O ? Piece.X : Piece.O);
                int retScore = minMax(player == Piece.O ? Piece.X : Piece.O, depth - 1, board, move,true);
                board.undoMove(move);
                if (retScore <= maxScore) {
                    maxScore = retScore;
                }
                board.undoMove(move);
            }
            return maxScore;

        }
    }


    public ArrayList<Move> getNextMoveMinMaxAlphaBeta(int maxDepth, Board board, Move lastMove) {
        ArrayList<Move> bestMoves = new ArrayList<>();
        int maxScore = WORSE_SCORE; // Start with the worst possible score for the maximizer
        int alpha = WORSE_SCORE;     // Initialize alpha
        int beta = BEST_SCORE;      // Initialize beta
        ArrayList<Move> possibleMoves = board.getValidMoves(lastMove);

        Piece opponentPiece = (cpuMark == Piece.O) ? Piece.X : Piece.O;

/*
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<ABRunnable> runs = new ArrayList<>();
        int midPoint = possibleMoves.size()/2;
        List<Move> move1 = possibleMoves.subList(0, midPoint);
        List<Move> move2 = possibleMoves.subList(midPoint, possibleMoves.size());

        ABRunnable run1 = new ABRunnable(move1, cpuMark, maxDepth, board.clone(),true, alpha, beta);
        Thread thread1 = new Thread(run1);
        threads.add(thread1);
        runs.add(run1);

        ABRunnable run2 = new ABRunnable(move2, cpuMark, maxDepth, board.clone(),true, alpha, beta);
        Thread thread2 = new Thread(run2);
        threads.add(thread2);
        runs.add(run2);

        thread1.start();
        thread2.start();

        int i = 0;
        int score = Integer.MIN_VALUE;
        ArrayList<Move> move = new ArrayList<>();

        while(!threads.isEmpty()) {
            for(Thread thread : threads) {
                if(!thread.isAlive()) {
                    int idx = threads.indexOf(thread);
                    int ret = runs.get(idx).getScore();
                    if(ret > score) {
                        bestMoves.clear();
                        bestMoves.addAll(runs.get(idx).getMoves());
                        score = ret;
                    }
                    else if(ret == score){
                        bestMoves.addAll(runs.get(idx).getMoves());
                    }

                    runs.remove(idx);
                    threads.remove(thread);

                    break;
                }
            }

        }

*/
        // The top level is always maximizing for us
        for (Move move : possibleMoves) {

            board.playMove(move, cpuMark);
            int score = minMaxAlphaBeta(opponentPiece, (int)(maxDepth*2/possibleMoves.size()) - 1, board, move, false, alpha, beta);
            board.undoMove(move);

            if (score == maxScore) {
                bestMoves.add(move);
            } else if (score > maxScore) {
                maxScore = score;
                bestMoves.clear();
                bestMoves.add(move);
            }
            //Root setting the alpha score, beta in recursive
            alpha = Math.max(alpha, score);
        }






        return bestMoves;
    }

    //is public because it is needed for multi-threading
    //it's more than likely a bad way to implement
    //tell me if there's better
    public int minMaxAlphaBeta(Piece player, int depth, Board board, Move lastPlayedMove, boolean isMaxing, int alpha, int beta) {

        Piece winner = board.isDone();
        if (depth <= 0 || winner != Piece.EMPTY) {
            return evaluateB(board, player);
        }

        ArrayList<Move> possibleMoves = board.getValidMoves(lastPlayedMove);
        Piece nextPlayer = (player == Piece.O) ? Piece.X : Piece.O;

        if (isMaxing) {
            int maxScore = WORSE_SCORE;
            for (Move move : possibleMoves) {
                board.playMove(move, player);
                int score = minMaxAlphaBeta(nextPlayer, depth - 1, board, move, false, alpha, beta);
                board.undoMove(move);

                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, maxScore);

                if (beta <= alpha) {
                    break;
                }
            }
            return maxScore;

        } else {
            int minScore = BEST_SCORE;
            for (Move move : possibleMoves) {
                board.playMove(move, player);
                int score = minMaxAlphaBeta(nextPlayer, depth - 1, board, move, true, alpha, beta);
                board.undoMove(move);

                minScore = Math.min(minScore, score);
                beta = Math.min(beta, minScore);

                if (beta <= alpha) {
                    break;
                }
            }
            return minScore;
        }
    }

    private int evaluateBoard(Board board, Piece player, Move move) {
        Piece opponent = (cpuMark == Piece.X) ? Piece.O : Piece.X;

        // 1. Check for Overall Game Win/Loss/Draw
        Piece overallWinner = board.isDone();
        if (overallWinner == cpuMark) {
            return OVERALL_WIN_SCORE;
        }
        if (overallWinner == opponent) {
            return -OVERALL_WIN_SCORE;
        }
        if (overallWinner == Piece.TIE) {
            return 0;
        }

        // --- Game is ongoing ---

        int currentScore = 0;

        // 2. Evaluate based on won Sub-Boards

        int subScore = 0;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Piece subWinner = board.getSubBoard(r, c).getWinner();
                if (subWinner == cpuMark) {
                    subScore += SUB_BOARD_WIN_SCORE;
                } else if (subWinner == opponent) {
                    subScore -= SUB_BOARD_WIN_SCORE;
                }

                for(int i = 0; i < 3; i++) {
                    currentScore += evaluateRow(board.getSubBoard(r, c), i, player) * 10;
                    currentScore += evaluateCol(board.getSubBoard(r, c), i, player) * 10;
                    currentScore += evaluateCorner(board.getSubBoard(r, c), player) * 5;
                }
                currentScore += evaluateDiagonal(board.getSubBoard(r, c), player)*10;
            }

            if(player == cpuMark)
                return currentScore + subScore;
            else
                return -currentScore + subScore;

        }

        if(board.getSubBoard(1,1).getValueAt(1,1).equals(player)){
            currentScore += 50;
        } else{
            currentScore -= 50;
        }

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(board.getSubBoard(i, j).getValueAt(1, 1) == player) {
                    currentScore += 15;
                }
                else if(board.getSubBoard(i, j).getValueAt(1, 1) == opponent) {
                    currentScore -= 15;
                }
            }
        }



        return currentScore;
    }

    public Piece getCpuMark() {
        return cpuMark;
    }

    public void setCpuMark(Piece cpuMark) {
        this.cpuMark = cpuMark;
    }


    public int evaluateRow(SubBoard subBoard, int row, Piece player) {
        int score = 0;
        for(int i = 0; i < 3; i++) {
            if(subBoard.getValueAt(row, i) == player)
                score ++;
            else if(subBoard.getValueAt(row, i) == (player==Piece.X?Piece.O:Piece.X))
                return 0;
        }

        return score;
    }

    public int evaluateCol(SubBoard subBoard, int col, Piece player) {
        int score = 0;
        for(int i = 0; i < 3; i++) {
            if(subBoard.getValueAt(i, col) == player)
                score++;
            else if(subBoard.getValueAt(i, col) == (player==Piece.X?Piece.O:Piece.X))
                return 0;
        }
        return score;
    }

    public int evaluateCorner(SubBoard subBoard, Piece player) {
        int score = 0;

        if(subBoard.getValueAt(0,0) == player)
            score++;
        else if(subBoard.getValueAt(2,2) == player)
            score++;
        else if(subBoard.getValueAt(0,2) == player)
            score++;
        else if(subBoard.getValueAt(2,0) == player)
            score++;

        return score;
    }

    public int evaluateDiagonal(SubBoard subBoard, Piece player) {
        int score1 = 0;
        int score2 = 0;
        for(int i = 0; i < 3; i++) {
            if(subBoard.getValueAt(i, i) == player)
                score1++;
            else if(subBoard.getValueAt(i, i) != Piece.EMPTY) {
                score1 = 0;
                break;
            }
        }


        for(int i = 0; i < 3; i++) {
            if(subBoard.getValueAt(2-i, i) == player)
                score2++;
            else if(subBoard.getValueAt(2-i, i) != Piece.EMPTY) {
                score2 = 0;
                break;
            }
        }

        return score1 + score2;
    }

    public int evaluateB(Board board, Piece player) {
        int score = 0;
        final Piece opponent = player==Piece.X?Piece.O:Piece.X;

        final int SUB_BOARD_SCORE = 10;
        final int CENTER_SUB_BOARD_SCORE = 15;
        final int SUB_BOARD_CENTER = 5;
        final int COL_ROW_SCORE = 2;


        for(int r = 0; r < 3; r++) {
            for(int c = 0; c < 3; c++) {
                SubBoard subBoard = board.getSubBoard(r, c);
                //check for subBoard wins
                if(subBoard.getWinner() == player) {
                    score += SUB_BOARD_SCORE;
                }
                else if (subBoard.getWinner() == opponent)
                    score -= SUB_BOARD_SCORE;

                //check for center of SubBoard
                if(subBoard.getValueAt(1, 1) == player)
                    score += SUB_BOARD_CENTER;
                else if(subBoard.getValueAt(1, 1) == opponent)
                    score -= SUB_BOARD_CENTER;

                for(int i = 0; i < 3; i++) {
                    //check for columns
                    if (evaluateCol(subBoard, i, player) == 2)
                        score += COL_ROW_SCORE;
                    //check for rows
                    if(evaluateRow(subBoard, i, player) == 2)
                        score += COL_ROW_SCORE;

                    if (evaluateCol(subBoard, i, opponent) == 2)
                        score -= COL_ROW_SCORE;
                    //check for rows
                    if(evaluateRow(subBoard, i, opponent) == 2)
                        score -= COL_ROW_SCORE;
                }

                //check for diagonals
                int a = 0;
                for(int i = 0; i < 3; i++) {
                    if(subBoard.getValueAt(i, 2-i) == player)
                        a++;
                    else if(subBoard.getValueAt(i, 2-i) != Piece.EMPTY) {
                        a = 0;
                        break;
                    }
                }
                if(a == 2)
                    score += COL_ROW_SCORE;
                a = 0;

                for(int i = 0; i < 3; i++) {
                    if(subBoard.getValueAt(i, i) == player)
                        a++;
                    else if(subBoard.getValueAt(i, i) != Piece.EMPTY) {
                        a = 0;
                        break;
                    }
                }
                if(a == 2)
                    score+= COL_ROW_SCORE;


            }
        }
        if(board.getSubBoard(1, 1).getWinner() == player)
            score += CENTER_SUB_BOARD_SCORE+1;

        //diagonal for big board
        SubBoard subBoard = board.getEquivalentSubBoard();
        int a = 0;
        int b = 0;
        for(int i = 0; i < 3; i++) {
            if(subBoard.getValueAt(i, i) == player)
                a++;
            else if(subBoard.getValueAt(i, i) != Piece.EMPTY) {
                a = 0;
                break;
            }
        }
        if(a == 2)
            score += 4;
        a = 0;

        for(int i = 0; i < 3; i++) {
            if(subBoard.getValueAt(i, i) == player)
                a++;
            else if(subBoard.getValueAt(i, i) != Piece.EMPTY) {
                a = 0;
                break;
            }
        }
        if(a == 2)
            score+= 4;

        if(player == cpuMark)
            return score;
        else
            return -score;
    }

    public int evaluateSubBoard(SubBoard subBoard, Piece player) {
        int score = 0;

        return score;
    }
}
