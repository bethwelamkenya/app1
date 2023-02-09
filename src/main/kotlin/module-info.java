module com.bethwelamkenya.app2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires java.sql;


    opens com.bethwelamkenya.app2 to javafx.fxml;
    exports com.bethwelamkenya.app2;
    opens com.bethwelamkenya.app2.controllers to javafx.fxml;
    exports com.bethwelamkenya.app2.controllers;
}