public class PegSolitaireLogic {

    private int size;
    private String type;
    private int[][] board;

    public PegSolitaireLogic(int size, String type) {
        this.size = size;
        this.type = type;
        board = new int[size][size];
        startNewGame();
    }

    public void startNewGame() {

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                board[r][c] = 1;
            }
        }

        board[size / 2][size / 2] = 0;

        if (type.equals("Diamond")) {

            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {

                    if (Math.abs(r - size / 2) + Math.abs(c - size / 2) > size / 2)
                        board[r][c] = -1;
                }
            }
        }

        if (type.equals("English")) {

            int k = size / 3;

            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {

                    if ((r < k || r >= size - k) && (c < k || c >= size - k))
                        board[r][c] = -1;
                }
            }
        }
    }

    public int getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public int getCell(int r, int c) {
        return board[r][c];
    }

    public boolean isValidMove(int r1, int c1, int r2, int c2) {

        if (board[r1][c1] != 1 || board[r2][c2] != 0)
            return false;

        if (r1 == r2 && Math.abs(c1 - c2) == 2) {
            int mid = (c1 + c2) / 2;
            return board[r1][mid] == 1;
        }

        if (c1 == c2 && Math.abs(r1 - r2) == 2) {
            int mid = (r1 + r2) / 2;
            return board[mid][c1] == 1;
        }

        return false;
    }

    public boolean makeMove(int r1, int c1, int r2, int c2) {

        if (!isValidMove(r1, c1, r2, c2))
            return false;

        int midR = (r1 + r2) / 2;
        int midC = (c1 + c2) / 2;

        board[r1][c1] = 0;
        board[midR][midC] = 0;
        board[r2][c2] = 1;

        return true;
    }

    public int getPegCount() {

        int count = 0;

        for (int[] row : board)
            for (int cell : row)
                if (cell == 1)
                    count++;

        return count;
    }

    public boolean checkWin() {
        return getPegCount() == 1;
    }

    public boolean hasMovesLeft() {

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {

                if (board[r][c] == 1) {

                    if (tryMove(r,c,r,c+2) ||
                            tryMove(r,c,r,c-2) ||
                            tryMove(r,c,r+2,c) ||
                            tryMove(r,c,r-2,c))
                        return true;
                }
            }
        }

        return false;
    }

    private boolean tryMove(int r1,int c1,int r2,int c2){

        if(r2<0 || c2<0 || r2>=size || c2>=size)
            return false;

        return isValidMove(r1,c1,r2,c2);
    }
}