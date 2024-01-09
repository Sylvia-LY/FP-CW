package com.example.hellofx;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.stage.Stage;


public class StudentController implements Initializable {


    @FXML
    private TableColumn<?, ?> allStudentsColCourseType;

    @FXML
    private TableColumn<?, ?> allStudentsColDateOfBirth;

    @FXML
    private TableColumn<?, ?> allStudentsColDateOfEntry;

    @FXML
    private TableColumn<?, ?> allStudentsColEmail;

    @FXML
    private TableColumn<?, ?> allStudentsColFamilyName;

    @FXML
    private TableColumn<?, ?> allStudentsColGivenName;

    @FXML
    private TableColumn<?, ?> allStudentsColHasDebts;

    @FXML
    private TableColumn<?, ?> allStudentsColHasEnrolled;

    @FXML
    private TableColumn<?, ?> allStudentsColModeOfStudy;

    @FXML
    private TableColumn<?, ?> allStudentsColStudentNumber;

    @FXML
    private TableView<Student> allStudentsTable;

    @FXML
    private ToggleGroup allStudentsTableToggleGroup;

    @FXML
    private TableColumn<?, ?> moduleMarkColCredit;

    @FXML
    private TableColumn<?, ?> moduleMarkColHasPassed;

    @FXML
    private TableColumn<?, ?> moduleMarkColLevel;

    @FXML
    private TableColumn<?, ?> moduleMarkColMark;

    @FXML
    private TableColumn<?, ?> moduleMarkColModuleName;

    @FXML
    private TableColumn<?, ?> moduleMarkColModuleNumber;

    @FXML
    private Button studentAgeRangeBtn;

    @FXML
    private TextField studentBeginningLetterBox;

    @FXML
    private ComboBox<CourseType> studentCourseTypeComboBox;

    @FXML
    private RadioButton studentDebtFreeRadio;

    @FXML
    private TextField studentEndLetterBox;

    @FXML
    private RadioButton studentEnrolledRadio;

    @FXML
    private RadioButton studentFailedRadio;

    @FXML
    private Label studentFullNameLabel;

    @FXML
    private Button studentGivenNameRangeBtn;

    @FXML
    private RadioButton studentHasDebtsRadio;

    @FXML
    private TextField studentMaxBox;

    @FXML
    private TextField studentMinBox;

    @FXML
    private ComboBox<ModeOfStudy> studentModeOfStudyComboBox;

    @FXML
    private TableView<Mark> studentModuleMarkTable;

    @FXML
    private Label studentNumberLabel;

    @FXML
    private RadioButton studentPassedRadio;

    @FXML
    private TextField studentSearchBox;

    @FXML
    private ComboBox<String> studentSearchComboBox;

    @FXML
    private Label studentTotalCreditsLabel;

    @FXML
    private Label studentTotalMarksLabel;

    @FXML
    private RadioButton studentUnenrolledRadio;

    @FXML
    private RadioButton studentAllRadio;

    @FXML
    private Label studentModulesLabel;


    public void studentShowAllStudents() {

        allStudentsColStudentNumber.setCellValueFactory(new PropertyValueFactory<>("studentNumber"));
        allStudentsColGivenName.setCellValueFactory(new PropertyValueFactory<>("givenName"));
        allStudentsColFamilyName.setCellValueFactory(new PropertyValueFactory<>("familyName"));
        allStudentsColDateOfBirth.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        allStudentsColEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        allStudentsColCourseType.setCellValueFactory(new PropertyValueFactory<>("courseType"));
        allStudentsColDateOfEntry.setCellValueFactory(new PropertyValueFactory<>("dateOfEntry"));
        allStudentsColModeOfStudy.setCellValueFactory(new PropertyValueFactory<>("modeOfStudy"));
        allStudentsColHasEnrolled.setCellValueFactory(new PropertyValueFactory<>("hasEnrolled"));
        allStudentsColHasDebts.setCellValueFactory(new PropertyValueFactory<>("hasDebts"));
        allStudentsTable.setItems(Main.registerM.getAllStudents());

        allStudentsTable.getSelectionModel().selectFirst();
        allStudentsTableSelect();
    }

    public void studentShowFiltered(List<Student> filteredList) {
        allStudentsTable.setItems(FXCollections.observableList(filteredList));
    }

    public void allStudentsTableRadio() {
        if (studentEnrolledRadio.isSelected()) {
            studentShowFiltered(Main.registerM.getEnrolledStudents());
        }
        else if (studentUnenrolledRadio.isSelected()) {
            studentShowFiltered(Main.registerM.getUnenrolledStudents());
        }
        else if (studentDebtFreeRadio.isSelected()) {
            studentShowFiltered(Main.registerM.getDebtFreeStudents());
        }
        else if (studentHasDebtsRadio.isSelected()) {
            studentShowFiltered(Main.registerM.getStudentsWithDebts());
        }
    }

    public void allStudentsTableCourseType() {
        CourseType value = studentCourseTypeComboBox.getValue();
        studentShowFiltered(
                Main.registerM.getStudentsByCourseType(value)
        );
    }

    public void allStudentsTableModeOfStudy() {
        ModeOfStudy value = studentModeOfStudyComboBox.getValue();
        studentShowFiltered(
                Main.registerM.getStudentsByModeOfStudy(value)
        );
    }


    public void allStudentsTableSearch() {
        FilteredList<Student> filtered = new FilteredList<>(Main.registerM.getStudents(), p -> true);

        studentSearchBox.textProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal.isEmpty()) { filtered.setPredicate(p -> true); }
            else {
                String sub = newVal.trim().toLowerCase();
                switch (studentSearchComboBox.getValue()) {
                    case "Student Number":
                        filtered.setPredicate(
                                s -> s.getStudentNumber().contains(sub)
                        );
                        break;
                    case "Full Name":
                        filtered.setPredicate(
                                s -> s.getGivenName().toLowerCase().contains(sub)
                                        || s.getFamilyName().toLowerCase().contains(sub)
                        );
                        break;
                    case "Email":
                        filtered.setPredicate(
                                s -> s.getEmail().toLowerCase().contains(sub)
                        );
                        break;
                }
            }
            allStudentsTable.setItems(filtered);

        });
    }


    private Student student;

    public void allStudentsTableSelect() {
        if (allStudentsTable.getSelectionModel().getSelectedItem() != null) {
            student = allStudentsTable.getSelectionModel().getSelectedItem();

            studentNumberLabel.setText("Student#: "+student.getStudentNumber());
            studentFullNameLabel.setText(student.getGivenName()+" "+student.getFamilyName());

            double totalMarks = Main.registerM.calculateTotalMarks(student);
            int totalCredits = Main.registerM.calculateTotalCredits(student);
            studentTotalMarksLabel.setText("Total Mark(s): "+totalMarks);
            studentTotalCreditsLabel.setText("Total Credit(s): "+totalCredits);

            studentAllRadio.setSelected(true);

            studentModulesLabel.setText(student.getModuleMark().size()+" Module(s)");

            ObservableList<Mark> moduleMarkList = FXCollections.observableList(student.getModuleMark());
            moduleMarkColModuleNumber.setCellValueFactory(new PropertyValueFactory<>("moduleNumber"));
            moduleMarkColModuleName.setCellValueFactory(new PropertyValueFactory<>("moduleName"));
            moduleMarkColLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
            moduleMarkColMark.setCellValueFactory(new PropertyValueFactory<>("mark"));
            moduleMarkColCredit.setCellValueFactory(new PropertyValueFactory<>("credit"));
            moduleMarkColHasPassed.setCellValueFactory(new PropertyValueFactory<>("hasPassed"));

            studentModuleMarkTable.setItems(moduleMarkList);

        }
    }

    public  void allStudentsTableAgeRange() {
        String min = studentMinBox.getText().replaceAll("[^0-9]]","");
        String max = studentMaxBox.getText().replaceAll("[^0-9]]","");

        if (min.isEmpty()) {
            min = "0";
        }
        if (max.isEmpty()) {
            max = "122";
        }

        studentShowFiltered(
                Main.registerM.getStudentsByAgeRange(
                        Integer.parseInt(min), Integer.parseInt(max)
                )
        );
    }


    public  void allStudentsTableGivenNameRange() {
        String beginningLetter = studentBeginningLetterBox
                .getText().substring(0,1).toUpperCase();
        String endLetter = studentEndLetterBox
                .getText().substring(0,1).toUpperCase();

        if (beginningLetter.isEmpty()) {
            beginningLetter = "A";
        }
        if (endLetter.isEmpty()) {
            endLetter = "Z";
        }
        studentShowFiltered(
                Main.registerM.getStudentsByGivenNameRange(
                        beginningLetter, endLetter
                )
        );


    }

    public void moduleMarkTableRadio() {
        if (studentAllRadio.isSelected()) {
            studentModuleMarkTable.setItems(
                    FXCollections.observableList(student.getModuleMark())
            );
        }
        else if (studentPassedRadio.isSelected()) {
            studentModuleMarkTable.setItems(
                    FXCollections.observableList(Main.registerM.getPassedModules(student))
            );
        }
        else if (studentFailedRadio.isSelected()) {
            studentModuleMarkTable.setItems(
                    FXCollections.observableList(Main.registerM.getFailedModules(student))
            );
        }
    }



    public void setRemoveBtn() {
        Connection con = DBConnection.getDBConnection();
        Alert alert;
        try {
            alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "are you sure you want to remove the selected student entry? " +
                            "this action cannot be undone",
                    ButtonType.YES,
                    ButtonType.CANCEL);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {

                con.setAutoCommit(false);

                String selectSql = "select id from students where student_number = '"
                        +student.getStudentNumber()
                        +"'";
                ResultSet result = con.prepareStatement(selectSql).executeQuery();
                int studentID=0;
                while (result.next()) {
                    studentID = result.getInt("id");
                }

                Statement statement = con.createStatement();
                String deleteSql = "delete from marks where student_id = "+studentID;
                statement.executeUpdate(deleteSql);

                deleteSql = "delete from students where student_number = '"
                        +student.getStudentNumber()
                        +"'";
                statement.executeUpdate(deleteSql);

                con.commit();

                alert = new Alert(Alert.AlertType.INFORMATION);
                String alertText = "student entry removed successfully";
                alert.setContentText(alertText);
                alert.showAndWait();

                Main.registerM.getAllStudents();
                studentShowAllStudents();
                Main.moduleC.moduleShowAllModules();

            }
            else {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("cancelled");
                alert.showAndWait();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setAddBtn() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("add.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();

            stage.setTitle("add.fxml");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.studentC = this;
        studentShowAllStudents();
        studentCourseTypeComboBox.getItems().addAll(CourseType.values());
        studentModeOfStudyComboBox.getItems().addAll(ModeOfStudy.values());
        studentSearchComboBox.getItems().addAll("Student Number", "Full Name", "Email");
        studentSearchComboBox.setValue("Student Number");





    }
}