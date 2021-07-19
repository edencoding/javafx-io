package com.edencoding.controllers;

import com.edencoding.SQLiteExampleApp;
import com.edencoding.dao.PersonDAO;
import com.edencoding.models.Person;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class Controller {

    public TableView<Person> exampleTable;
    public TableColumn<Person, Integer> idColumn;
    public TableColumn<Person, String> firstNameColumn;
    public TableColumn<Person, String> lastNameColumn;
    public TableColumn<Person, Integer> ageColumn;
    public Button editButton;
    public Button deleteButton;

    public void initialize() {
        exampleTable.setItems(PersonDAO.getPersons());

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        editButton.disableProperty().bind(Bindings.isEmpty(exampleTable.getSelectionModel().getSelectedItems()));
        deleteButton.disableProperty().bind(Bindings.isEmpty(exampleTable.getSelectionModel().getSelectedItems()));
    }

    public void handleExitButtonClicked(ActionEvent event) {
        Platform.exit();
        event.consume();
    }

    public void addPerson(ActionEvent event) {
        Dialog<Person> addPersonDialog = createPersonDialog(null);
        Optional<Person> result = addPersonDialog.showAndWait();
        result.ifPresent(person ->
                PersonDAO.insertPerson(
                        person.getFirstName(),
                        person.getLastName(),
                        person.getAge()
                ));
        event.consume();
    }

    public void deletePerson(ActionEvent event) {
        for (Person person : exampleTable.getSelectionModel().getSelectedItems()) {
            PersonDAO.delete(person.getId());
        }
        event.consume();
    }

    public void editPerson(ActionEvent event) {
        if (exampleTable.getSelectionModel().getSelectedItems().size() != 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Person editing error");
            alert.setContentText("One person must be selected when editing");
        } else {
            Dialog<Person> dialog = createPersonDialog(exampleTable.getSelectionModel().getSelectedItem());
            Optional<Person> optionalPerson = dialog.showAndWait();
            optionalPerson.ifPresent(PersonDAO::update);
        }
        event.consume();
    }

    private Dialog<Person> createPersonDialog(Person person) {
        //create the dialog itself
        Dialog<Person> dialog = new Dialog<>();
        dialog.setTitle("Add Dialog");
        if(person==null){
            dialog.setHeaderText("Add a new person to the database");
        } else {
            dialog.setHeaderText("Edit a database record");
        }
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Stage dialogWindow = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogWindow.getIcons().add(new Image(SQLiteExampleApp.class.getResource("img/EdenCodingIcon.png").toExternalForm()));

        //create the form for the user to fill in
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField firstName = new TextField();
        firstName.setPromptText("First Name");
        TextField lastName = new TextField();
        lastName.setPromptText("Last Name");
        TextField age = new TextField();
        age.setPromptText("Age");
        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstName, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastName, 1, 1);
        grid.add(new Label("Age:"), 0, 2);
        grid.add(age, 1, 2);
        dialog.getDialogPane().setContent(grid);


        //disable the OK button if the fields haven't been filled in
        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(
                Bindings.createBooleanBinding(() -> firstName.getText().trim().isEmpty(), firstName.textProperty())
                        .or(Bindings.createBooleanBinding(() -> lastName.getText().trim().isEmpty(), lastName.textProperty())
                                .or(Bindings.createBooleanBinding(() -> age.getText().trim().isEmpty(), age.textProperty())
                                )));

        //ensure only numeric input (integers) in age text field
        UnaryOperator<TextFormatter.Change> numberValidationFormatter = change -> {
            if (change.getText().matches("\\d+") || change.getText().equals("")) {
                return change; //if change is a number or if a deletion is being made
            } else {
                change.setText(""); //else make no change
                change.setRange(    //don't remove any selected text either.
                        change.getRangeStart(),
                        change.getRangeStart()
                );
                return change;
            }
        };
        age.setTextFormatter(new TextFormatter<Object>(numberValidationFormatter));

        //make sure the dialog returns a Person if it's available
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                int id = -1;
                if (person != null) id = person.getId();
                return new Person(firstName.getText(), lastName.getText(), Integer.valueOf(age.getText()), id);
            }
            return null;
        });

        //if a record is supplied, use it to fill in the fields automatically
        if (person != null) {
            firstName.setText(person.getFirstName());
            lastName.setText(person.getLastName());
            age.setText(String.valueOf(person.getAge()));
        }

        return dialog;
    }
}
