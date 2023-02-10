module com.bethwelamkenya.app2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires java.sql;
    requires itextpdf;
    requires io;


    opens com.bethwelamkenya.app2 to javafx.fxml;
    exports com.bethwelamkenya.app2;
    opens com.bethwelamkenya.app2.controllers to javafx.fxml;
    exports com.bethwelamkenya.app2.controllers;
    opens com.bethwelamkenya.app2.models to javafx.base;
    exports com.bethwelamkenya.app2.models;
}