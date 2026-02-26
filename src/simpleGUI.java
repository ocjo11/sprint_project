import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class simpleGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("GUI Demo");

        // Pane for layout
        Pane root = new Pane();

        // Text
        Label label = new Label("Welcome to the GUI Demo!");
        label.setLayoutX(20);
        label.setLayoutY(20);

        // Line
        Line line = new Line(20, 50, 300, 50);
        line.setStroke(Color.BLUE);
        line.setStrokeWidth(2);

        // CheckBox
        CheckBox checkBox = new CheckBox("Enable Option");
        checkBox.setLayoutX(20);
        checkBox.setLayoutY(70);

        // RadioButtons
        RadioButton option1 = new RadioButton("Option 1");
        RadioButton option2 = new RadioButton("Option 2");
        option1.setLayoutX(20);
        option1.setLayoutY(100);
        option2.setLayoutX(120);
        option2.setLayoutY(100);

        ToggleGroup group = new ToggleGroup();
        option1.setToggleGroup(group);
        option2.setToggleGroup(group);

        // Add all to root
        root.getChildren().addAll(label, line, checkBox, option1, option2);

        // Scene and stage
        Scene scene = new Scene(root, 350, 150);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}