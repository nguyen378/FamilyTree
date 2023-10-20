module donutnv.familytree {
    requires javafx.controls;
    requires javafx.fxml;

    opens donutnv.familytree to javafx.fxml;
    exports donutnv.familytree;
}
