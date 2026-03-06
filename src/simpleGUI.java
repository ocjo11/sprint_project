import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class simpleGUI extends Application {

    private PegSolitaireLogic game;
    private Pane root;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private int cellSize = 60;

    private ComboBox<Integer> sizeBox;
    private ComboBox<String> typeBox;

    private Label message;

    @Override
    public void start(Stage stage) {

        root = new Pane();
        setupControls();

        Scene scene = new Scene(root, 700, 600);

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

        message = new Label("");
        message.setLayoutX(550);
        message.setLayoutY(20);

        root.getChildren().addAll(sizeLabel,sizeBox,typeLabel,typeBox,newGame,message);
    }

    private void startNewGame() {

        game = new PegSolitaireLogic(sizeBox.getValue(),typeBox.getValue());

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

                peg.setCenterX(100 + c*cellSize);
                peg.setCenterY(100 + r*cellSize);

                int row=r;
                int col=c;

                peg.setOnMouseClicked(e->handleClick(row,col));

                root.getChildren().add(peg);
            }
        }
    }

    private void handleClick(int r,int c){

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