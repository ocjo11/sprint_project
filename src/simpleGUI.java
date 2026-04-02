import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

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

    @Override
    public void start(Stage stage) {

        root = new Pane();
        setupControls();

        Scene scene = new Scene(root, 750, 650);

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

        Button newGame = new Button("New Game");
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

        Button autoMove = new Button("Auto Move");
        autoMove.setLayoutX(250);
        autoMove.setLayoutY(55);
        autoMove.setOnAction(e -> {
            if (game != null) {

                boolean moved = game.playTurn();

                drawBoard();

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

        Button randomBtn = new Button("Randomize");
        randomBtn.setLayoutX(350);
        randomBtn.setLayoutY(55);
        randomBtn.setOnAction(e -> {
            if (game != null) {
                game.randomizeBoard();
                drawBoard();
            }
        });

        message = new Label("");
        message.setLayoutX(550);
        message.setLayoutY(20);

        root.getChildren().addAll(
                sizeLabel, sizeBox,
                typeLabel, typeBox,
                newGame,
                modeLabel, modeBox,
                autoMove, randomBtn,
                message
        );
    }

    private void startNewGame() {

        int size = sizeBox.getValue();
        String type = typeBox.getValue();

        if (modeBox.getValue().equals("Manual")) {
            game = new ManualGame(size, type);
        } else {
            game = new AutomatedGame(size, type);
        }

        selectedRow = -1;
        selectedCol = -1;

        message.setText("Game started");

        drawBoard();
    }

    private void drawBoard() {

        root.getChildren().removeIf(n -> n instanceof Circle);

        for(int r=0;r<game.getSize();r++){
            for(int c=0;c<game.getSize();c++){

                int cell = game.getCell(r,c);

                if(cell==-1)
                    continue;

                Circle peg = new Circle(20);

                if(cell==1)
                    peg.setFill(Color.DARKBLUE);
                else
                    peg.setFill(Color.LIGHTGRAY);

                if(r==selectedRow && c==selectedCol)
                    peg.setStroke(Color.RED);

                peg.setCenterX(140 + c*cellSize);
                peg.setCenterY(140 + r*cellSize);

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

                if(game.checkWin()){

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("You Win!");
                    alert.setContentText("Only one peg left!");
                    alert.showAndWait();

                }else if(!game.hasMovesLeft()){

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Game Over");
                    alert.setContentText("No moves remaining.");
                    alert.showAndWait();
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