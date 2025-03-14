import java.util.ArrayList;

public class CPU {
    Piece cpuMark;
    final int WORSE_SCORE = Integer.MIN_VALUE;
    final int BEST_SCORE = Integer.MAX_VALUE;

    public CPU(Piece cpuMark) {
        this.cpuMark = cpuMark;
    }

    public ArrayList<Move> getNextMoveMinMax(SubBoard subBoard, int maxDepth, Board board) {
        ArrayList<Move> moves = new ArrayList<>();
        int max = WORSE_SCORE;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                Move move = new Move(i,j);
                if (subBoard.getValueAt(i, j) == Piece.EMPTY) {
                    subBoard.play(move, cpuMark);
                    int ret = minMax(subBoard, cpuMark == Piece.O ? Piece.X : Piece.O, 1, maxDepth, board);
                    subBoard.play(move, Piece.EMPTY);
                    if (ret == max) {
                        moves.add(move);
                    } else if(ret > max) {
                        moves.clear();
                        moves.add(move);
                        max = ret;
                    }
                }
            }
        }
        //System.out.println(max);
        return moves;
    }

    private int minMax(SubBoard subBoard, Piece player, int depth, int maxDepth, Board board) {
        int currentScore = subBoard.evaluate(player);
        if(maxDepth == depth)
            return currentScore;
        if(currentScore !=  0 || subBoard.checkIfBoardFull())
            return currentScore;

        int max = player==cpuMark?WORSE_SCORE : BEST_SCORE;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {

                Move move = new Move(i,j);
                if(subBoard.getValueAt(i, j) == Piece.EMPTY) {
                    subBoard.play(move, player);
                    depth++;
                    SubBoard nSubBoard = board.getSubBoard(i, j);
                    if(!nSubBoard.isDone()) {
                        if (player == cpuMark)
                            max = Math.max(max, minMax(nSubBoard, player == Piece.X ? Piece.O : Piece.X, depth, maxDepth, board));
                        else
                            max = Math.min(max, minMax(nSubBoard, player == Piece.X ? Piece.O : Piece.X, depth, maxDepth, board));
                    }
                    else {
                        for(int k = 0; k < 9; k++) {
                            int a = 0;
                            if (k != 0 && k % 3 == 0)
                                a++;
                            nSubBoard = board.getBoards()[k][a];
                            if(player == cpuMark)
                                max = Math.max(max, minMax(nSubBoard, player == Piece.X ? Piece.O : Piece.X, depth, maxDepth, board));
                            else
                                max = Math.min(max, minMax(nSubBoard, player == Piece.X ? Piece.O : Piece.X, depth, maxDepth, board));
                        }
                    }

                    subBoard.play(move, Piece.EMPTY);
                }
            }
        }
        return max;
    }


    public ArrayList<Move> getNextMoveAB(SubBoard subBoard, Board board) {
        int alpha = WORSE_SCORE;
        int beta = BEST_SCORE;

        int bestMoveValue = WORSE_SCORE;
        ArrayList<Move> bestMoves = new ArrayList<>();

        Piece enemy = (cpuMark.equals(Piece.X)) ? Piece.O : Piece.X;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (subBoard.getValueAt(i, j).equals(Piece.EMPTY)) {
                    subBoard.setValueAt(i, j, cpuMark);
                    int moveValue = alphaBeta(enemy, subBoard, alpha, beta, board);
                    subBoard.setValueAt(i, j, Piece.EMPTY);

                    bestMoveValue = getBestMoveValue(bestMoveValue, bestMoves, i, j, moveValue);
                }
            }
        }
        return bestMoves;
    }

    //La partie récursive de alphaBeta
    private int alphaBeta(Piece player, SubBoard subBoard, int alpha, int beta, Board board) {
        int currentScore = subBoard.evaluate(cpuMark);
        if (currentScore != 0 || subBoard.checkIfBoardFull()) {
            return currentScore;
        }

        boolean maxing = player == cpuMark;
        int bestScore = maxing ? WORSE_SCORE : BEST_SCORE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (subBoard.getValueAt(i, j) == Piece.EMPTY) {
                    subBoard.setValueAt(i, j, player);
                    Piece enemy = player.equals(Piece.O) ? Piece.X : Piece.O;
                    int score = alphaBeta(enemy, subBoard, alpha, beta, board);
                    subBoard.setValueAt(i, j, Piece.EMPTY);

                    if (maxing) {
                        bestScore = Math.max(score, bestScore);
                        alpha = Math.max(alpha, bestScore);
                    } else {
                        bestScore = Math.min(score, bestScore);
                        beta = Math.min(beta, bestScore);
                    }

                    if (beta <= alpha) {
                        return bestScore;
                    }
                }
            }
        }
        return bestScore;
    }

    private int getBestMoveValue(int bestMoveValue, ArrayList<Move> bestMovesList, int i, int j, int moveValue) {
        if (moveValue > bestMoveValue) {
            bestMoveValue = moveValue;
            bestMovesList.clear();
            Move bestMove = new Move(i, j);
            bestMovesList.add(bestMove);
        } else if (moveValue == bestMoveValue) {
            Move bestMove = new Move(i, j);
            bestMovesList.add(bestMove);
        }
        return bestMoveValue;
    }

    public Piece getCpuMark() {
        return cpuMark;
    }

    public void setCpuMark(Piece cpuMark) {
        this.cpuMark = cpuMark;
    }
}
