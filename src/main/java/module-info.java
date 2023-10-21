module donutnv.familytree {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens donutnv.familytree to javafx.fxml;
    exports donutnv.familytree;
    requires org.neo4j.driver;
}
