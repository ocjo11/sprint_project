import java.util.*;

public class AutomatedGame extends PegSolitaireGame {

    // ==================== CONSTRUCTOR ====================
    public AutomatedGame(int size, String type) {
        super(size, type);
    }

    // ==================== METHODS ====================
    @Override
    public boolean makeMove(int r1, int c1, int r2, int c2) {
        if (!isValidMove(r1, c1, r2, c2))
            return false;

        applyMove(r1, c1, r2, c2);
        return true;
    }

    @Override
    public boolean playTurn() {
        List<int[]> moves = getAllValidMoves();
        if (moves.isEmpty())
            return false;

        int[] move = moves.get(new Random().nextInt(moves.size()));
        applyMove(move[0], move[1], move[2], move[3]);
        return true;
    }

    // ==================== HELPER METHODS ====================
    private List<int[]> getAllValidMoves() {
        List<int[]> moves = new ArrayList<>();

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (board[r][c] == 1) {
                    int[][] dirs = {{0, 2}, {0, -2}, {2, 0}, {-2, 0}};
                    for (int[] d : dirs) {
                        int r2 = r + d[0];
                        int c2 = c + d[1];
                        if (isValidMove(r, c, r2, c2))
                            moves.add(new int[]{r, c, r2, c2});
                    }
                }
            }
        }

        return moves;
    }
}