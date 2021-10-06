import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class tekstbehandler extends Application {
    //Labels
    private Label filenameLabel;

    //Text fields
    private TextField filenameField;

    //Text area
    private TextArea textArea;

    //Buttons
    private Button filenameSubmit;
    private Button fileSave;
    private Button fileReset;

    //Variables
    final int MAX_WIDTH = 300;
    final int MAX_HEIGHT = 300;
    String filepath = "";

    //Filechooser
    final FileChooser fileChooser = new FileChooser();

    //Make stage public
    Stage vindu;

    public void start(Stage vindu) throws Exception {
        //Labels
        filenameLabel = new Label("Filnavn: ");

        //TextField
        filenameField = new TextField();

        //TextArea
        textArea = new TextArea();
        textArea.setMinHeight(MAX_HEIGHT - (MAX_HEIGHT/10));

        //Buttons
        filenameSubmit = new Button("Choose file");
        filenameSubmit.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        fileSave = new Button("Save");
        fileSave.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        fileReset = new Button("Restart");
        fileReset.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        //Create grid pane
        GridPane gridpane = new GridPane();

        //Padding
        gridpane.setPadding(new Insets(10, 10, 10, 10));

        //Size
        gridpane.setMinSize(MAX_WIDTH, MAX_HEIGHT);

        //Create gridpane columns
        for (int i = 0; i <= 10; i++) {
            ColumnConstraints column = new ColumnConstraints(MAX_WIDTH/10);
            gridpane.getColumnConstraints().add(column);
            RowConstraints row = new RowConstraints(MAX_HEIGHT/10);
            gridpane.getRowConstraints().add(row);
        }

        //Arranging all the elements
        gridpane.add(filenameLabel, 0, 0, 2, 1);
        gridpane.add(filenameField, 2, 0, 6, 1);
        gridpane.add(textArea, 0, 1, 11, 9);
        gridpane.add(filenameSubmit, 8, 0, 3, 1);
        gridpane.add(fileSave, 0, 10, 4, 1);
        gridpane.add(fileReset, 7, 10, 4, 1);

        //Button actions
        filenameSubmit.setOnAction(e -> chooseFile(e));
        fileSave.setOnAction(e -> saveFile(e));
        fileReset.setOnAction(e -> resetProgram(e));

        //New scene
        Scene scene = new Scene(gridpane);

        //Title
        vindu.setTitle("Uke12");

        //Adding scene
        vindu.setScene(scene);

        //Display content
        vindu.show();
    }

    //Runs javafx
    public static void main(String[] args) {
        launch(args);
    }

    //Choose file
    private void chooseFile(ActionEvent action) {
        String fileContent = "";
        File file = fileChooser.showOpenDialog(vindu);
        try {
            filenameField.setText(file.getName());
            filepath = file.getPath();
            Scanner scanner = new Scanner(new File(filepath));
            while (scanner.hasNextLine()) {
                fileContent += scanner.nextLine() + "\n";
            }
            textArea.setText(fileContent);
            scanner.close();
        } catch (Exception e) {
            showError("You need to choose a file!");
        }
    }

    //Saves the file
    private void saveFile(ActionEvent action) {
        Alert alert = new Alert(AlertType.ERROR);
        try {
            PrintWriter printWriter = new PrintWriter(filepath);
            printWriter.println(textArea.getText());
            printWriter.close();
            textArea.setText("");
            filenameField.setText("");
            JOptionPane.showMessageDialog(null, "File saved successfully!");
        } catch(Exception e) {
            showError("Try again!");
        }

    }

    //Reset fields in program
    private void resetProgram(ActionEvent action) {
        try {
            textArea.setText("");
            filenameField.setText("");
            filepath = "";
        } catch(Exception e) {
            showError("Try again");
        }
    }

    //Show errorcode
    private void showError(String melding) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Feilmelding");
        alert.setHeaderText("En feil har oppst�tt");
        alert.setContentText(melding);
        alert.showAndWait();
    }
}