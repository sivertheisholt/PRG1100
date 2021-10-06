import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

public class oppgave extends Application {
    // Labels
    private Label tasklistlabel;
    private Label filterLabel;
    private Label editTaskLabel;
    private Label descriptionEditLabel;
    private Label addTaskLabel;
    private Label descriptionAddLabel;

    // TableView
    private TableView<Task> taskTable;

    // Buttons
    private Button saveButton;
    private Button deleteButton;
    private Button addButton;

    // Text area
    private TextArea descriptionEditTextArea;
    private TextArea descriptionAddTextArea;

    // Checkbox
    private CheckBox notDoneCheckbox;
    private CheckBox setAsDoneEditCheckbox;
    private CheckBox setAsDoneAddCheckbox;

    // Variables
    final int MAX_WIDTH = 300;
    final int MAX_HEIGHT = 600;
    final int columns = 5;
    final int rows = 15;
    int currentID;
    int addCounter = 0;
    final ObservableList<Task> masterList = FXCollections.observableArrayList();
    private FilteredList<Task> filteredList;

    // Make stage public
    Stage window;

    public void start(Stage window) throws Exception {

        loadData();

        // Labels
        Label test123 = new Label("Test");
        tasklistlabel = new Label("Task List");
        tasklistlabel.setStyle("-fx-font: 24 arial;");
        filterLabel = new Label("Filter: ");
        filterLabel.setStyle("-fx-font: 16 arial;");
        editTaskLabel = new Label("Edit Task");
        editTaskLabel.setStyle("-fx-font: 24 arial;");
        descriptionEditLabel = new Label("Description: ");
        descriptionEditLabel.setStyle("-fx-font: 16 arial;");
        addTaskLabel = new Label("Add Task");
        addTaskLabel.setStyle("-fx-font: 24 arial;");
        descriptionAddLabel = new Label("Description: ");
        descriptionAddLabel.setStyle("-fx-font: 16 arial;");

        // Buttons
        saveButton = new Button("Save");
        saveButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        deleteButton = new Button("Delete");
        deleteButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        addButton = new Button("Add");
        addButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // TextArea
        descriptionEditTextArea = new TextArea();
        descriptionEditTextArea.setPrefHeight(MAX_HEIGHT / rows);
        descriptionEditTextArea.setPrefWidth(MAX_WIDTH / columns * 2);
        descriptionAddTextArea = new TextArea();
        descriptionAddTextArea.setPrefHeight(MAX_HEIGHT / rows);
        descriptionAddTextArea.setPrefWidth(MAX_WIDTH / columns * 2);

        // Checkbox
        notDoneCheckbox = new CheckBox("Not done");
        setAsDoneEditCheckbox = new CheckBox("Done");
        setAsDoneAddCheckbox = new CheckBox("Done");

        // TableView
        taskTable = new TableView<>();

        // Setup columns in tableview
        TableColumn<Task, Integer> idColumn = new TableColumn<>("ID"); // Make a new column
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id")); // Connecting the column with constructor

        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Description"); // Make a new column
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description")); // Connecting the column with
                                                                                          // constructor

        TableColumn<Task, Boolean> doneColumn = new TableColumn<>("Done"); // Make a new column
        doneColumn.setCellValueFactory(new PropertyValueFactory<>("done")); // Connecting the column with constructor

        // Make filteredlist
        filteredList = new FilteredList<Task>(masterList, t -> true);

        // Create TableView
        taskTable.setItems(filteredList);
        taskTable.getColumns().addAll(idColumn, descriptionColumn, doneColumn);
        // TableView settings
        taskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Create grid pane
        GridPane gridpane = new GridPane();

        // Padding
        gridpane.setPadding(new Insets(10, 10, 10, 10));

        // Size
        gridpane.setMinSize(MAX_WIDTH, MAX_HEIGHT);

        // Create gridpane columns
        for (int i = 0; i < columns; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0 / columns);
            gridpane.getColumnConstraints().add(column);
        }
        // Create gridpane rows
        for (int i = 0; i < rows; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / rows);
            gridpane.getRowConstraints().add(row);
        }

        // Arranging all the elements (item, column, row, columnStretch, rowStretch);
        // Label
        gridpane.add(tasklistlabel, 0, 0, 2, 1);
        gridpane.add(filterLabel, 0, 1);
        gridpane.add(editTaskLabel, 0, 7, 2, 1);
        gridpane.add(descriptionEditLabel, 0, 8, 2, 1);
        gridpane.add(addTaskLabel, 3, 7, 2, 1);
        gridpane.add(descriptionAddLabel, 3, 8, 2, 1);

        // TableView
        gridpane.add(taskTable, 0, 2, 7, 5);

        // TextArea
        gridpane.add(descriptionEditTextArea, 0, 9, 2, 1);
        gridpane.add(descriptionAddTextArea, 3, 9, 2, 1);

        // Checkbox
        gridpane.add(notDoneCheckbox, 1, 1);
        gridpane.add(setAsDoneEditCheckbox, 0, 10);

        // button
        gridpane.add(saveButton, 0, 11);
        gridpane.add(deleteButton, 1, 11);
        gridpane.add(addButton, 3, 10, 2, 1);

        // Button actions
        saveButton.setOnAction(e -> updateList(e));
        deleteButton.setOnAction(e -> deleteTask(e));
        addButton.setOnAction(e -> addTask(e));

        // New scene
        Scene scene = new Scene(gridpane);

        // Title
        window.setTitle("Oblig2");

        // Adding scene
        window.setScene(scene);

        // Display content
        window.show();

        // Listener on checkbox
        notDoneCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (notDoneCheckbox.isSelected()) {
                    filteredList.setPredicate(task -> !task.isDone());
                } else {
                    filteredList.setPredicate(task -> true);
                }
            }
        });

        // Listener for tableview
        taskTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Task>() {
            @Override
            public void changed(ObservableValue<? extends Task> observableValue, Task task, Task t1) {
                selected(taskTable.getSelectionModel().getSelectedItem().description,
                        taskTable.getSelectionModel().getSelectedItem().id,
                        taskTable.getSelectionModel().getSelectedItem().done == true);
            }
        });
    }

    // Runs javafx
    public static void main(String[] args) {
        launch(args);
    }

    // Loads data into the table for the first time from the database
    private void loadData() throws SQLException {
        List<String> tempList = new ArrayList<String>();
        Connection con = DriverManager.getConnection("jdbc:sqlite:demo.db");
        System.out.println("Connection true!");
        Statement statement = con.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS task (id integer, description string, done int)");
        ResultSet rs = statement.executeQuery("select * from task");
        while (rs.next()) {
            // read the result set
            String currentRow = String.valueOf(rs.getInt("id")) + ";" + rs.getString("description") + ";"
                    + String.valueOf(rs.getInt("done"));
            tempList.add(currentRow);
        }
        System.out.println("Made list! Closing connection...");
        con.close();
        System.out.println("Returning list...");

        for (int i = 0; i < tempList.size(); i++) {
            String line = tempList.get(i);
            String[] parts = line.split(";");
            masterList.add(
                    new Task(Integer.parseInt(parts[0]), parts[1], (Integer.parseInt(parts[2]) == 1) ? true : false));
            if (Integer.parseInt(parts[0]) > addCounter) {
                addCounter = Integer.parseInt(parts[0]);
            }
        }
        System.out.print("Done!");
    }

    // Update the arraylist with new values
    private void updateList(ActionEvent action) {
        try {
            boolean done = (setAsDoneEditCheckbox.isSelected() ? true : false);
            String currentDescription = descriptionEditTextArea.getText();
            for (int i = 0; i < filteredList.size(); i++) {
                if (filteredList.get(i).id == currentID) {
                    masterList.set(i, new Task(currentID, currentDescription, done));
                }
            }
            taskTable.refresh();
            updateDB(currentDescription, done);
        } catch (Exception e) {
            showError("Invalid input!");
        }
    }

    // Add to DB
    private void addToDB(String description, boolean done) throws SQLException {
        String query = "INSERT INTO task(id, description, done) VALUES(" + addCounter + "," + "'" + description + "'"
                + "," + done + ")";
        Connection con = DriverManager.getConnection("jdbc:sqlite:demo.db");
        System.out.println("Connection true!");
        Statement statement = con.createStatement();
        statement.executeUpdate(query);
        System.out.println("Database updated!");
        con.close();
    }

    // Update DB
    private void updateDB(String description, boolean done) throws SQLException {
        String query = "UPDATE task SET description= " + "'" + description + "'" + ", done= " + done + " WHERE id= "
                + currentID;
        Connection con = DriverManager.getConnection("jdbc:sqlite:demo.db");
        System.out.println("Connection true!");
        Statement statement = con.createStatement();
        statement.executeUpdate(query);
        System.out.println("Database updated!");
        con.close();
    }

    private void deleteDB() throws SQLException {
        try {
            String query = "DELETE FROM task WHERE id=" + currentID;
            Connection con = DriverManager.getConnection("jdbc:sqlite:demo.db");
            System.out.println("Connection true!");
            Statement statement = con.createStatement();
            statement.executeUpdate(query);
            System.out.println("Database updated!");
            con.close();
        } catch (Exception e) {
            showError("You need to select a task!");
        }
    }

    // Delete task
    private void deleteTask(ActionEvent action) {
        try {
            for (int i = 0; i < filteredList.size(); i++) {
                if (filteredList.get(i).id == currentID) {
                    masterList.remove(i);
                }
            }
            deleteDB();
        } catch (Exception e) {
            showError("Something");
        }
    }

    // Add task
    private void addTask(ActionEvent action) {
        try {
            // Updating arraylist
            String currentDescription = descriptionAddTextArea.getText();
            addCounter++;
            masterList.add(new Task(addCounter, currentDescription, false));
            descriptionAddTextArea.setText("");
            // Updating DB
            addToDB(currentDescription, false);
        } catch (Exception e) {
            showError("Something");
        }
    }

    private void selected(String description, int id, boolean done) {
        descriptionEditTextArea.setText(description);
        currentID = id;
        setAsDoneEditCheckbox.setSelected(done);
    }

    // Show errorcode
    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Something unexpected happen!");
        alert.setContentText(message);
        alert.showAndWait();
    }
}