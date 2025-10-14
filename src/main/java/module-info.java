module se233.contra {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires javafx.media;


    opens se233.contra to javafx.fxml;
    exports se233.contra;
}