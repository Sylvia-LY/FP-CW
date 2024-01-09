module com.example.hellofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires java.sql;


    opens com.example.hellofx to javafx.fxml;
    exports com.example.hellofx;
}