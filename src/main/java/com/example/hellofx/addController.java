package com.example.hellofx;

import javafx.fxml.Initializable;
import java.net.URL;
import java.sql.*;
import java.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

public class addController implements Initializable {

    @FXML
    private Button addBtn;

    @FXML
    private ComboBox<CourseType> addCourseTypeComboBox;

    @FXML
    private DatePicker addDateOfBirthPicker;

    @FXML
    private DatePicker addDateOfEntryPicker;

    @FXML
    private TextField addEmailBox;

    @FXML
    private TextField addFamilyNameBox;

    @FXML
    private TextField addGivenNameBox;

    @FXML
    private ComboBox<Boolean> addHasDebtsComboBox;

    @FXML
    private ComboBox<Boolean> addHasEnrolledComboBox;

    @FXML
    private ComboBox<Integer> addLevelComboBox;

    @FXML
    private ComboBox<ModeOfStudy> addModeOfStudyComboBox;

    @FXML
    private TextField addStudentNumberBox;

    @FXML
    private Button clearBtn;

    @FXML
    private Button moduleMarkBtn;

    private Map<String, String> moduleMarks = new HashMap<>();
    Integer level;
    public void addModuleMark() {
        moduleMarks.clear();

        if (addLevelComboBox.getValue() ==null) {
            level = 4;
        }
        else {
            level = addLevelComboBox.getValue();
        }

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        var modules = Main.registerM.getModulesByLevel(level);
        Map<TextField, String> marks = new HashMap<>();
        for (int i = 0; i < modules.size(); i++) {
            String moduleName = modules.get(i).getModuleName();
            TextField textField = new TextField();
            grid.add(new Label(moduleName + ": "), 0, i);
            grid.add(textField, 1, i);
            marks.put(textField, moduleName);
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                for (TextField textField: marks.keySet()) {
                    if (!textField.getText().isEmpty()) {
                        moduleMarks.put(marks.get(textField), textField.getText());
                    }
                }
            }
            return null;
        });


        dialog.showAndWait();

    }


    public void setClearBtn() {
        addStudentNumberBox.setText("");
        addGivenNameBox.setText("");
        addFamilyNameBox.setText("");
        addEmailBox.setText("");
        moduleMarks.clear();
    }


    Integer studentID;
    public void setAddBtn() {
        try(Connection con = DBConnection.getDBConnection()) {

            Alert alert;
            if (addStudentNumberBox.getText().isEmpty()
                    || addGivenNameBox.getText().isEmpty()
                    || addFamilyNameBox.getText().isEmpty()
                    || addEmailBox.getText().isEmpty()
                    || addCourseTypeComboBox.getSelectionModel().getSelectedItem() == null
                    || moduleMarks.isEmpty()
                    || addDateOfBirthPicker.getValue() == null
                    || addDateOfEntryPicker.getValue() == null
                    || addModeOfStudyComboBox.getSelectionModel().getSelectedItem() == null
                    || addHasEnrolledComboBox.getSelectionModel().getSelectedItem() == null
                    || addHasDebtsComboBox.getSelectionModel().getSelectedItem() == null) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("please fill in all blank fields");
                alert.showAndWait();
            }

            else {
                String selectSql = "select student_number from students WHERE student_number = '"
                        +addStudentNumberBox.getText()
                        +"'";
                ResultSet result = con.createStatement().executeQuery(selectSql);

                if (result.next()) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("student#"+addStudentNumberBox.getText()+" already exists");
                    alert.showAndWait();
                }

                else {
                    try (PreparedStatement prep =
                                 con.prepareStatement(
                                         "INSERT INTO students (" +
                                                 "student_number,given_name,family_name,date_of_birth,email," +
                                                 "course_type,date_of_entry,mode_of_study,has_enrolled,has_debts" +
                                                 ") VALUES (?,?,?,?,?,?,?,?,?,?)",
                                         Statement.RETURN_GENERATED_KEYS
                                 )
                    ) {
                        prep.setString(1, addStudentNumberBox.getText());
                        prep.setString(2, addGivenNameBox.getText());
                        prep.setString(3, addFamilyNameBox.getText());
                        prep.setDate(4, java.sql.Date.valueOf(addDateOfBirthPicker.getValue()));
                        prep.setString(5, addEmailBox.getText());
                        prep.setString(6, String.valueOf(addCourseTypeComboBox.getSelectionModel().getSelectedItem()));
                        prep.setDate(7, java.sql.Date.valueOf(addDateOfEntryPicker.getValue()));
                        prep.setString(8, String.valueOf(addModeOfStudyComboBox.getSelectionModel().getSelectedItem()));
                        prep.setBoolean(9, addHasEnrolledComboBox.getSelectionModel().getSelectedItem());
                        prep.setBoolean(10, addHasDebtsComboBox.getSelectionModel().getSelectedItem());
                        prep.executeUpdate();

                        ResultSet rs = prep.getGeneratedKeys();
                        while (rs.next()) {
                            studentID = rs.getInt(1);
                        }

                        addModuleMarkToDB();

                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText("new student entry has been added to the system");
                        alert.showAndWait();

                        Main.registerM.getAllStudents();
                        Main.studentC.studentShowAllStudents();
                        Main.moduleC.moduleShowAllModules();

                        Stage stage = (Stage) addBtn.getScene().getWindow();
                        stage.close();


                    }
                }


            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void addModuleMarkToDB() {

        Connection con = DBConnection.getDBConnection();
        try {
            PreparedStatement prep = con.prepareStatement(
                    "insert into marks (" +
                            "student_id,module_id,mark,has_passed" +
                            ") values (?,?,?,?)"
            );

            for (var entry: moduleMarks.entrySet()) {
                Boolean hasPassed = false;
                Integer moduleID;

                var module = Main.registerM.getModulesByLevel(level)
                        .stream()
                        .filter(m -> m.getModuleName().equals(entry.getKey()))
                        .toList()
                        .getFirst();

                moduleID = module.getModuleID();

                if (Double.valueOf(entry.getValue())>=module.getThresholdMark()) {
                    hasPassed = true;
                }

                prep.setInt(1, studentID);
                prep.setInt(2, moduleID);
                prep.setDouble(3, Double.valueOf(entry.getValue()));
                prep.setBoolean(4, hasPassed);
                prep.addBatch();
            }
            prep.executeBatch();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addCourseTypeComboBox.getItems().addAll(CourseType.values());
        addModeOfStudyComboBox.getItems().addAll(ModeOfStudy.values());
        addHasEnrolledComboBox.getItems().addAll(true, false);
        addHasDebtsComboBox.getItems().addAll(true, false);
        addLevelComboBox.getItems().addAll(4, 5, 6);


    }
}


