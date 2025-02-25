module mp2i.sncf {
    requires org.json;

    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.desktop;

    opens mp2i.sncf.controllers to javafx.fxml;
    opens mp2i.sncf to javafx.fxml;

    exports mp2i.sncf.controllers;
    exports mp2i.sncf;
}