import java.util.*;
import java.io.*;

public abstract class PegSolitaireGame {

    // ==================== FIELDS ====================
    protected int size;
    protected String type;
    protected int[][] board;
    protected List<Move> moveHistory = new ArrayList<>();
    protected boolean isRecording = false;

    // ==================== CONSTRUCTOR ====================
    public PegSolitaireGame(int size, String type) {
        this.size = size;
        this.type = type;
        board = new int[size][size];
        startNewGame();
    }

    // ==================== BOARD INITIALIZATION ====================
    public void startNewGame() {
        // Initialize all cells as pegs (1)
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                board[r][c] = 1;

        // Set center peg as empty (0)
        board[size / 2][size / 2] = 0;

        // Apply board type patterns
        if (type.equals("Diamond")) {
            createDiamondPattern();
        } else if (type.equals("English")) {
            createEnglishPattern();
        } else if (type.equals("Hexagon")) {
            createHexagonPattern();
        }
    }

    private void createDiamondPattern() {
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                if (Math.abs(r - size / 2) + Math.abs(c - size / 2) > size / 2)
                    board[r][c] = -1;
    }

    private void createEnglishPattern() {
        int k = size / 3;
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                if ((r < k || r >= size - k) && (c < k || c >= size - k))
                    board[r][c] = -1;
    }

    private void createHexagonPattern() {
        int center = size / 2;

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                int rowDist = Math.abs(r - center);
                int colDist = Math.abs(c - center);
                int maxColDist = getMaxColumnDistanceForHexagon(rowDist);

                if (colDist > maxColDist) {
                    board[r][c] = -1;
                }
            }
        }
    }

    private int getMaxColumnDistanceForHexagon(int rowDist) {
        if (size == 5) {
            if (rowDist <= 1) return 2;
            else if (rowDist == 2) return 1;
            else return -1;
        } else if (size == 7) {
            if (rowDist <= 1) return 3;
            else if (rowDist == 2) return 2;
            else if (rowDist == 3) return 1;
            else return -1;
        } else { // size == 9
            if (rowDist <= 1) return 4;
            else if (rowDist == 2) return 3;
            else if (rowDist == 3) return 2;
            else if (rowDist == 4) return 1;
            else return -1;
        }
    }

    // ==================== GETTERS ====================
    public int getSize() { return size; }
    public String getType() { return type; }
    public int getCell(int r, int c) { return board[r][c]; }

    // ==================== GAME LOGIC ====================
    public boolean isValidMove(int r1, int c1, int r2, int c2) {
        // Check if destination is within bounds
        if (r2 < 0 || c2 < 0 || r2 >= size || c2 >= size)
            return false;

        // Check if source has peg and destination is empty
        if (board[r1][c1] != 1 || board[r2][c2] != 0)
            return false;

        // Check horizontal move (2 steps left or right)
        if (r1 == r2 && Math.abs(c1 - c2) == 2) {
            return board[r1][(c1 + c2) / 2] == 1;
        }

        // Check vertical move (2 steps up or down)
        if (c1 == c2 && Math.abs(r1 - r2) == 2) {
            return board[(r1 + r2) / 2][c1] == 1;
        }

        return false;
    }

    protected void applyMove(int r1, int c1, int r2, int c2) {
        int midR = (r1 + r2) / 2;
        int midC = (c1 + c2) / 2;

        // Remove the moving peg and the jumped peg
        board[r1][c1] = 0;
        board[midR][midC] = 0;

        // Place peg in the destination
        board[r2][c2] = 1;

        // Record move if recording is enabled
        if (isRecording) {
            moveHistory.add(new Move(r1, c1, r2, c2));
        }
    }

    public void randomizeBoard() {
        Random rand = new Random();

        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                if (board[r][c] != -1)
                    board[r][c] = rand.nextBoolean() ? 1 : 0;

        if (isRecording) {
            moveHistory.add(new Move("RANDOMIZE"));
        }
    }

    // ==================== GAME STATUS ====================
    public int getPegCount() {
        int count = 0;
        for (int[] row : board)
            for (int cell : row)
                if (cell == 1) count++;
        return count;
    }

    public boolean checkWin() {
        return getPegCount() == 1;
    }

    public boolean hasMovesLeft() {
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                if (board[r][c] == 1)
                    if (isValidMove(r, c, r, c + 2) ||
                            isValidMove(r, c, r, c - 2) ||
                            isValidMove(r, c, r + 2, c) ||
                            isValidMove(r, c, r - 2, c))
                        return true;
        return false;
    }

    // ==================== SAVE & LOAD METHODS ====================
    public void saveGame(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Move m : moveHistory) {
                writer.println(m.toString());
            }
            System.out.println("Saved " + moveHistory.size() + " moves to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveBoardState(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(size);
            writer.println(type);

            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    writer.print(board[r][c] + " ");
                }
                writer.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadBoardState(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            int savedSize = Integer.parseInt(scanner.nextLine());
            String savedType = scanner.nextLine();

            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    board[r][c] = scanner.nextInt();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveFullGame(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Save initial board state
            writer.println("BOARD_STATE");
            writer.println(size);
            writer.println(type);

            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    writer.print(board[r][c] + " ");
                }
                writer.println();
            }

            // Save all moves
            writer.println("MOVES");
            for (Move m : moveHistory) {
                writer.println(m.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearHistory() {
        moveHistory.clear();
    }

    // ==================== ABSTRACT METHODS ====================
    public abstract boolean makeMove(int r1, int c1, int r2, int c2);
    public abstract boolean playTurn();
}