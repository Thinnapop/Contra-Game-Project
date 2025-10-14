module se233.contra {
    requires javafx.controls;
    requires javafx.media;
    requires org.slf4j;
    requires ch.qos.logback.classic;

    opens se233.contra to javafx.fxml;
    exports se233.contra;
    exports se233.contra.controller;
    exports se233.contra.model;
    exports se233.contra.model.entity;
    exports se233.contra.view;
    exports se233.contra.util;
}