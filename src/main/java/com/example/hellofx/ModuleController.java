package com.example.hellofx;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;

public class ModuleController implements Initializable {

    @FXML
    private TableColumn<?, ?> allModulesColCreditValue;

    @FXML
    private TableColumn<?, ?> allModulesColLevel;

    @FXML
    private TableColumn<?, ?> allModulesColModuleName;

    @FXML
    private TableColumn<?, ?> allModulesColModuleNumber;

    @FXML
    private TableColumn<?, ?> allModulesColThresholdMark;

    @FXML
    private TableView<Module> allModulesTable;

    @FXML
    private RadioButton moduleAboveAverageRadio;

    @FXML
    private RadioButton moduleAllRadio;

    @FXML
    private Label moduleAverageMarkLabel;

    @FXML
    private RadioButton moduleBelowAverageRadio;

    @FXML
    private ComboBox<CourseType> moduleCourseTypeComboBox;

    @FXML
    private RadioButton moduleFailedRadio;

    @FXML
    private Label moduleMaxLabel;

    @FXML
    private Label moduleMinLabel;

    @FXML
    private Label modulePassRateLabel;

    @FXML
    private RadioButton modulePassedRadio;

    @FXML
    private TableView<RowData> moduleStudentTable;

    @FXML
    private Label moduleStudentsLabel;

    @FXML
    private TableColumn<?, ?> studentColCourseType;

    @FXML
    private TableColumn<?, ?> studentColCredit;

    @FXML
    private TableColumn<?, ?> studentColFamilyName;

    @FXML
    private TableColumn<?, ?> studentColGivenName;

    @FXML
    private TableColumn<?, ?> studentColHasPassed;

    @FXML
    private TableColumn<?, ?> studentColMark;

    @FXML
    private TableColumn<?, ?> studentColStudentNumber;

    @FXML
    private ToggleGroup studentTableToggleGroup;

    public void moduleShowAllModules() {

        allModulesColModuleNumber.setCellValueFactory(new PropertyValueFactory<>("moduleNumber"));
        allModulesColModuleName.setCellValueFactory(new PropertyValueFactory<>("moduleName"));
        allModulesColLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        allModulesColCreditValue.setCellValueFactory(new PropertyValueFactory<>("creditValue"));
        allModulesColThresholdMark.setCellValueFactory(new PropertyValueFactory<>("thresholdMark"));

        allModulesTable.setItems(Main.registerM.getAllModules());

        allModulesTable.getSelectionModel().selectFirst();
        allModulesTableSelect();
    }

    private Module module;
    private List<RowData> studentList;

    public void allModulesTableSelect() {
        if (allModulesTable.getSelectionModel().getSelectedItem() != null) {
            module = allModulesTable.getSelectionModel().getSelectedItem();

            moduleAllRadio.setSelected(true);

            studentList = Main.registerM.getStudentsByModule(module.getModuleNumber()).stream()
                    .map(s -> {
                        Mark m = s.getModuleMark()
                                .stream()
                                .filter(mark -> mark.getModuleNumber().equals(module.getModuleNumber()))
                                .toList()
                                .get(0);
                        Integer credit = 0;
                        if (m.getMark() >= module.getThresholdMark()) {
                            credit = module.getCreditValue();
                        }
                        return new RowData(
                                s.getStudentNumber(),
                                s.getGivenName(),
                                s.getFamilyName(),
                                s.getCourseType(),
                                m.getMark(),
                                credit,
                                m.getHasPassed()
                        );
                    })
                    .toList();

            studentColStudentNumber.setCellValueFactory(new PropertyValueFactory<>("studentNumber"));
            studentColGivenName.setCellValueFactory(new PropertyValueFactory<>("givenName"));
            studentColFamilyName.setCellValueFactory(new PropertyValueFactory<>("familyName"));
            studentColCourseType.setCellValueFactory(new PropertyValueFactory<>("courseType"));
            studentColMark.setCellValueFactory(new PropertyValueFactory<>("mark"));
            studentColHasPassed.setCellValueFactory(new PropertyValueFactory<>("hasPassed"));
            studentColCredit.setCellValueFactory(new PropertyValueFactory<>("credit"));
            moduleStudentTable.setItems(FXCollections.observableList(studentList));

            moduleStudentsLabel.setText(studentList.size()+" Student(s)");

            double passRate = Main.registerM.calculatePassRate(studentList);
            double averageMark = Main.registerM.calculateAverageMark(studentList);
            double maxMark = Main.registerM.findMaxMark(studentList);
            double minMark = Main.registerM.findMinMark(studentList);

            modulePassRateLabel.setText("Pass Rate(%): "+passRate);
            moduleAverageMarkLabel.setText("Average Mark: "+averageMark);
            moduleMaxLabel.setText("Max: "+maxMark);
            moduleMinLabel.setText("Min: "+minMark);



        }
    }



    public void studentTableRadio() {
        double averageMark = Main.registerM.calculateAverageMark(studentList);
        if (moduleAllRadio.isSelected()) {
            moduleStudentTable.setItems(FXCollections.observableList(studentList));
        }
        else if (modulePassedRadio.isSelected()) {
            moduleStudentTable.setItems(
                    FXCollections.observableList(
                            studentList.stream().filter(RowData::getHasPassed).toList()
                    )
            );
        }
        else if (moduleFailedRadio.isSelected()) {
            moduleStudentTable.setItems(
                    FXCollections.observableList(
                            studentList.stream().filter(s -> !s.getHasPassed()).toList()
                    )
            );

        }
        else if (moduleAboveAverageRadio.isSelected()) {
            moduleStudentTable.setItems(
                    FXCollections.observableList(
                            studentList.stream().filter(s -> s.getMark() >= averageMark).toList()
                    )
            );

        }
        else if (moduleBelowAverageRadio.isSelected()) {
            moduleStudentTable.setItems(
                    FXCollections.observableList(
                            studentList.stream().filter(s -> s.getMark() < averageMark).toList()
                    )
            );

        }
    }

    public void studentTableCourseType() {
        CourseType value = moduleCourseTypeComboBox.getValue();
        moduleStudentTable.setItems(
                FXCollections.observableList(
                        studentList.stream().filter(s -> s.getCourseType().equals(value)).toList()
                )
        );
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.moduleC = this;
        moduleShowAllModules();
        moduleCourseTypeComboBox.getItems().addAll(CourseType.values());


    }
}
