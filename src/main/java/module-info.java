module donutnv.familytree {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens donutnv.familytree to javafx.fxml;
    exports donutnv.familytree;
    requires org.neo4j.driver;
    requires java.desktop;
    requires java.logging;
    requires org.jgrapht.core;
    requires org.jgrapht.ext;
    requires org.jgrapht.demo;
}
