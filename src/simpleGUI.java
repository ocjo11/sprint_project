import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class simpleGUI extends Application {

    private PegSolitaireGame game;
    private Pane root;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private int cellSize = 60;

    private ComboBox<Integer> sizeBox;
    private ComboBox<String> typeBox;
    private ComboBox<String> modeBox;
    private Label message;
    private CheckBox recordBox;
    private Button saveNowBtn;
    private Button replayBtn;
    private Button newGame;
    private Button autoMove;
    private Button randomBtn;
    private Button saveReplayState;

    // Store the complete game state including randomize operations
    private List<GameState> recordedStates;
    private int[][] initialBoardState;
    private int initialSize;
    private String initialType;

    // Inner class to store game state after each action
    private class GameState {
        String action; // "MOVE" or "RANDOMIZE"
        int r1, c1, r2, c2; // for moves
        int[][] boardState; // full board state after action

        // Constructor for MOVE
        GameState(int r1, int c1, int r2, int c2, int[][] board) {
            this.action = "MOVE";
            this.r1 = r1;
            this.c1 = c1;
            this.r2 = r2;
            this.c2 = c2;
            this.boardState = copyBoard(board);
        }

        // Constructor for RANDOMIZE
        GameState(int[][] board) {
            this.action = "RANDOMIZE";
            this.boardState = copyBoard(board);
        }

        private int[][] copyBoard(int[][] original) {
            int[][] copy = new int[original.length][original.length];
            for (int i = 0; i < original.length; i++) {
                System.arraycopy(original[i], 0, copy[i], 0, original.length);
            }
            return copy;
        }
    }

    @Override
    public void start(Stage stage) {

        root = new Pane();
        setupControls();

        Scene scene = new Scene(root, 800, 750);

        stage.setTitle("Peg Solitaire");
        stage.setScene(scene);
        stage.show();
    }

    private void setupControls() {

        Label sizeLabel = new Label("Board Size:");
        sizeLabel.setLayoutX(20);
        sizeLabel.setLayoutY(20);

        sizeBox = new ComboBox<>();
        sizeBox.getItems().addAll(5,7,9);
        sizeBox.setValue(7);
        sizeBox.setLayoutX(100);
        sizeBox.setLayoutY(15);

        Label typeLabel = new Label("Board Type:");
        typeLabel.setLayoutX(200);
        typeLabel.setLayoutY(20);

        typeBox = new ComboBox<>();
        typeBox.getItems().addAll("English","Hexagon","Diamond");
        typeBox.setValue("English");
        typeBox.setLayoutX(280);
        typeBox.setLayoutY(15);

        newGame = new Button("New Game");
        newGame.setLayoutX(420);
        newGame.setLayoutY(15);
        newGame.setOnAction(e -> startNewGame());

        Label modeLabel = new Label("Mode:");
        modeLabel.setLayoutX(20);
        modeLabel.setLayoutY(60);

        modeBox = new ComboBox<>();
        modeBox.getItems().addAll("Manual","Automated");
        modeBox.setValue("Manual");
        modeBox.setLayoutX(100);
        modeBox.setLayoutY(55);

        autoMove = new Button("Auto Move");
        autoMove.setLayoutX(250);
        autoMove.setLayoutY(55);
        autoMove.setOnAction(e -> {
            if (game != null) {
                boolean moved = game.playTurn();
                drawBoard();

                // If recording, save the state after auto move
                if (recordBox.isSelected() && moved) {
                    saveCurrentGameStateAsMove(0, 0, 0, 0); // This needs the actual move coordinates
                }

                if (!moved || !game.hasMovesLeft()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);

                    if (game.checkWin()) {
                        alert.setHeaderText("You Win!");
                        alert.setContentText("Only one peg left!");
                    } else {
                        alert.setHeaderText("Game Over");
                        alert.setContentText("No moves remaining.");
                    }

                    alert.showAndWait();
                }
            }
        });

        randomBtn = new Button("Randomize");
        randomBtn.setLayoutX(350);
        randomBtn.setLayoutY(55);
        randomBtn.setOnAction(e -> {
            if (game != null) {
                game.randomizeBoard();
                drawBoard();

                // If recording, save the randomize action with the new board state
                if (recordBox.isSelected()) {
                    saveRandomizeState();
                    message.setText("Randomize recorded");
                }
            }
        });

        recordBox = new CheckBox("Record Game");
        recordBox.setLayoutX(20);
        recordBox.setLayoutY(600);

        // Recording logic
        recordBox.setOnAction(e -> {
            if (game == null) {
                message.setText("Start a game first!");
                recordBox.setSelected(false);
                return;
            }

            if (recordBox.isSelected()) {
                // Starting recording - initialize recording
                recordedStates = new ArrayList<>();
                saveInitialBoardState();
                message.setText("Recording started - make moves or randomize");
            } else {
                // Stopping recording and saving
                if (recordedStates != null && !recordedStates.isEmpty()) {
                    saveFullGameState("game_full.txt");
                    message.setText("Game saved to game_full.txt (" + recordedStates.size() + " actions)");
                } else {
                    message.setText("No actions to save");
                }
            }
        });

        saveNowBtn = new Button("Save Now");
        saveNowBtn.setLayoutX(120);
        saveNowBtn.setLayoutY(600);
        saveNowBtn.setOnAction(e -> {
            if (game != null && recordedStates != null && !recordedStates.isEmpty()) {
                saveFullGameState("game_full.txt");
                message.setText("Game saved to game_full.txt (" + recordedStates.size() + " actions)");
            } else if (game != null) {
                message.setText("No actions to save yet");
            } else {
                message.setText("Start a game first");
            }
        });

        replayBtn = new Button("Replay");
        replayBtn.setLayoutX(220);
        replayBtn.setLayoutY(600);

        replayBtn.setOnAction(e -> {
            if (game == null) {
                message.setText("Start a game first!");
                return;
            }

            message.setText("Replaying game...");

            // Disable buttons during replay
            newGame.setDisable(true);
            autoMove.setDisable(true);
            randomBtn.setDisable(true);
            recordBox.setDisable(true);
            saveNowBtn.setDisable(true);
            replayBtn.setDisable(true);
            modeBox.setDisable(true);
            sizeBox.setDisable(true);
            typeBox.setDisable(true);

            String savedMode = modeBox.getValue();

            new Thread(() -> {
                try {
                    loadAndReplayFullGame("game_full.txt", savedMode);

                    Platform.runLater(() -> {
                        message.setText("Replay complete!");
                        newGame.setDisable(false);
                        autoMove.setDisable(false);
                        randomBtn.setDisable(false);
                        recordBox.setDisable(false);
                        saveNowBtn.setDisable(false);
                        replayBtn.setDisable(false);
                        modeBox.setDisable(false);
                        sizeBox.setDisable(false);
                        typeBox.setDisable(false);
                    });

                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        message.setText("Error replaying: " + ex.getMessage());
                        newGame.setDisable(false);
                        autoMove.setDisable(false);
                        randomBtn.setDisable(false);
                        recordBox.setDisable(false);
                        saveNowBtn.setDisable(false);
                        replayBtn.setDisable(false);
                        modeBox.setDisable(false);
                        sizeBox.setDisable(false);
                        typeBox.setDisable(false);
                    });
                    ex.printStackTrace();
                }
            }).start();
        });

        saveReplayState = new Button("Save Replay State");
        saveReplayState.setLayoutX(320);
        saveReplayState.setLayoutY(600);
        saveReplayState.setOnAction(e -> {
            if (game != null && recordedStates != null && !recordedStates.isEmpty()) {
                saveFullGameState("replay_state.txt");
                message.setText("Replay state saved to replay_state.txt");
            } else if (game != null) {
                message.setText("No actions to save");
            } else {
                message.setText("Start a game first");
            }
        });

        message = new Label("");
        message.setLayoutX(450);
        message.setLayoutY(600);
        message.setPrefWidth(300);

        root.getChildren().addAll(
                sizeLabel, sizeBox,
                typeLabel, typeBox,
                newGame,
                modeLabel, modeBox,
                autoMove, randomBtn,
                recordBox, saveNowBtn, replayBtn, saveReplayState,
                message
        );
    }

    // Save the initial board state when recording starts
    private void saveInitialBoardState() {
        initialSize = game.getSize();
        initialType = game.getType();
        initialBoardState = new int[initialSize][initialSize];

        for (int r = 0; r < initialSize; r++) {
            for (int c = 0; c < initialSize; c++) {
                initialBoardState[r][c] = game.getCell(r, c);
            }
        }
    }

    // Save current game state as a move action
    private void saveCurrentGameStateAsMove(int r1, int c1, int r2, int c2) {
        int[][] currentBoard = new int[game.getSize()][game.getSize()];
        for (int r = 0; r < game.getSize(); r++) {
            for (int c = 0; c < game.getSize(); c++) {
                currentBoard[r][c] = game.getCell(r, c);
            }
        }
        recordedStates.add(new GameState(r1, c1, r2, c2, currentBoard));
    }

    // Save randomize action with current board state
    private void saveRandomizeState() {
        int[][] currentBoard = new int[game.getSize()][game.getSize()];
        for (int r = 0; r < game.getSize(); r++) {
            for (int c = 0; c < game.getSize(); c++) {
                currentBoard[r][c] = game.getCell(r, c);
            }
        }
        recordedStates.add(new GameState(currentBoard));
    }

    // Save full game state including all actions
    private void saveFullGameState(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Save initial board configuration
            writer.println("INITIAL_BOARD_STATE");
            writer.println(initialSize);
            writer.println(initialType);

            // Save the initial board layout
            for (int r = 0; r < initialSize; r++) {
                for (int c = 0; c < initialSize; c++) {
                    writer.print(initialBoardState[r][c] + " ");
                }
                writer.println();
            }

            // Save all actions (moves and randomizes)
            writer.println("ACTIONS");
            for (GameState state : recordedStates) {
                writer.println(state.action);
                if (state.action.equals("MOVE")) {
                    writer.println(state.r1 + "," + state.c1 + "," + state.r2 + "," + state.c2);
                }
                // Save the board state after this action
                for (int r = 0; r < initialSize; r++) {
                    for (int c = 0; c < initialSize; c++) {
                        writer.print(state.boardState[r][c] + " ");
                    }
                    writer.println();
                }
                writer.println("---"); // Separator between actions
            }

            System.out.println("Saved full game state to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load and replay full game state
    private void loadAndReplayFullGame(String filename, String savedMode) throws Exception {
        try (Scanner scanner = new Scanner(new File(filename))) {

            // Read initial board state
            String header = scanner.nextLine();
            if (!header.equals("INITIAL_BOARD_STATE")) {
                throw new Exception("Invalid file format");
            }

            int fileSize = Integer.parseInt(scanner.nextLine());
            String fileType = scanner.nextLine();

            // Create game with same parameters
            PegSolitaireGame replayGame;
            if (savedMode.equals("Manual")) {
                replayGame = new ManualGame(fileSize, fileType);
            } else {
                replayGame = new AutomatedGame(fileSize, fileType);
            }

            // Load the initial board state
            for (int r = 0; r < fileSize; r++) {
                for (int c = 0; c < fileSize; c++) {
                    int value = scanner.nextInt();
                    java.lang.reflect.Field boardField = PegSolitaireGame.class.getDeclaredField("board");
                    boardField.setAccessible(true);
                    int[][] board = (int[][]) boardField.get(replayGame);
                    board[r][c] = value;
                }
            }

            scanner.nextLine(); // consume newline
            scanner.nextLine(); // skip "ACTIONS" line

            // Update the GUI game reference to the initial state
            Platform.runLater(() -> {
                game = replayGame;
                drawBoard();
                message.setText("Loaded initial board state, starting replay...");
            });

            Thread.sleep(1000);

            // Replay all actions
            while (scanner.hasNextLine()) {
                String actionType = scanner.nextLine();

                if (actionType.equals("RANDOMIZE")) {
                    // Read the board state after randomize
                    int[][] boardAfter = new int[fileSize][fileSize];
                    for (int r = 0; r < fileSize; r++) {
                        for (int c = 0; c < fileSize; c++) {
                            boardAfter[r][c] = scanner.nextInt();
                        }
                    }
                    scanner.nextLine(); // consume newline
                    scanner.nextLine(); // skip "---" separator

                    // Apply the randomize and set the exact board state
                    Platform.runLater(() -> {
                        game.randomizeBoard();
                        // Set the exact board state from recording
                        try {
                            java.lang.reflect.Field boardField = PegSolitaireGame.class.getDeclaredField("board");
                            boardField.setAccessible(true);
                            int[][] board = (int[][]) boardField.get(game);
                            for (int r = 0; r < fileSize; r++) {
                                System.arraycopy(boardAfter[r], 0, board[r], 0, fileSize);
                            }
                            drawBoard();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        message.setText("Randomizing board...");
                    });
                    Thread.sleep(500);

                } else if (actionType.equals("MOVE")) {
                    // Read move coordinates
                    String moveLine = scanner.nextLine();
                    String[] parts = moveLine.split(",");
                    int r1 = Integer.parseInt(parts[0]);
                    int c1 = Integer.parseInt(parts[1]);
                    int r2 = Integer.parseInt(parts[2]);
                    int c2 = Integer.parseInt(parts[3]);

                    // Read the board state after move
                    int[][] boardAfter = new int[fileSize][fileSize];
                    for (int r = 0; r < fileSize; r++) {
                        for (int c = 0; c < fileSize; c++) {
                            boardAfter[r][c] = scanner.nextInt();
                        }
                    }
                    scanner.nextLine(); // consume newline
                    scanner.nextLine(); // skip "---" separator

                    // Apply the move and set exact board state
                    Platform.runLater(() -> {
                        game.makeMove(r1, c1, r2, c2);
                        // Set the exact board state from recording
                        try {
                            java.lang.reflect.Field boardField = PegSolitaireGame.class.getDeclaredField("board");
                            boardField.setAccessible(true);
                            int[][] board = (int[][]) boardField.get(game);
                            for (int r = 0; r < fileSize; r++) {
                                System.arraycopy(boardAfter[r], 0, board[r], 0, fileSize);
                            }
                            drawBoard();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        message.setText("Move: (" + r1 + "," + c1 + ") -> (" + r2 + "," + c2 + ")");
                    });
                    Thread.sleep(500);
                }
            }
        }
    }

    private void startNewGame() {
        int size = sizeBox.getValue();
        String type = typeBox.getValue();

        if (modeBox.getValue().equals("Manual")) {
            game = new ManualGame(size, type);
        } else {
            game = new AutomatedGame(size, type);
        }

        // Reset recording state
        if (recordBox != null) {
            recordBox.setSelected(false);
        }
        game.isRecording = false;
        game.clearHistory();

        // Clear recorded states
        recordedStates = null;
        initialBoardState = null;

        selectedRow = -1;
        selectedCol = -1;

        message.setText("Game started - " + modeBox.getValue() + " mode");
        drawBoard();
    }

    private void drawBoard() {

        root.getChildren().removeIf(n -> n instanceof Circle);

        int boardSize = game.getSize();
        int startX = 140;
        int startY = 120;

        if (boardSize == 9) {
            cellSize = 50;
            startX = 150;
            startY = 150;
        } else if (boardSize == 7) {
            cellSize = 60;
            startX = 140;
            startY = 150;
        } else {
            cellSize = 70;
            startX = 130;
            startY = 150;
        }

        for(int r=0; r<boardSize; r++){
            for(int c=0; c<boardSize; c++){

                int cell = game.getCell(r,c);

                if(cell==-1)
                    continue;

                Circle peg = new Circle(cellSize / 2.5);

                if(cell==1)
                    peg.setFill(Color.DARKBLUE);
                else
                    peg.setFill(Color.LIGHTGRAY);

                if(r==selectedRow && c==selectedCol)
                    peg.setStroke(Color.RED);

                peg.setCenterX(startX + c * cellSize);
                peg.setCenterY(startY + r * cellSize);

                int row=r;
                int col=c;

                peg.setOnMouseClicked(e->handleClick(row,col));

                root.getChildren().add(peg);
            }
        }
    }

    private void handleClick(int r,int c){

        if (modeBox.getValue().equals("Automated")) {
            message.setText("Automated mode: use Auto Move");
            return;
        }

        if(selectedRow==-1){

            if(game.getCell(r,c)==1){

                selectedRow=r;
                selectedCol=c;

                message.setText("Peg selected");
                drawBoard();
            }

        }else{

            boolean moved = game.makeMove(selectedRow,selectedCol,r,c);

            if(!moved){
                message.setText("Illegal move");
            }else{
                message.setText("Move successful");

                // If recording, save this move with the new board state
                if (recordBox.isSelected()) {
                    saveCurrentGameStateAsMove(selectedRow, selectedCol, r, c);
                }

                if(game.checkWin()){
                    new Alert(Alert.AlertType.INFORMATION,"You Win!").showAndWait();
                }else if(!game.hasMovesLeft()){
                    new Alert(Alert.AlertType.INFORMATION,"Game Over").showAndWait();
                }
            }

            selectedRow=-1;
            selectedCol=-1;

            drawBoard();
        }
    }

    public static void main(String[] args){
        launch();
    }
}