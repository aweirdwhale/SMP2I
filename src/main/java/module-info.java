module mp2i.sncf {
    requires org.json;
    requires javafx.controls;
    requires javafx.fxml;

    opens mp2i.sncf to javafx.fxml;
    exports mp2i.sncf;
}