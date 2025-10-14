module se233.contra {
    requires javafx.controls;
    requires javafx.fxml;


    opens se233.contra to javafx.fxml;
    exports se233.contra;
}