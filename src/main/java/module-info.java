module donutnv.familytree {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens donutnv.familytree to javafx.fxml;
    exports donutnv.familytree;
    exports donutnv.familytree.DataBase;
    exports donutnv.familytree.Controller;
    requires org.neo4j.driver;
    requires java.desktop;
    requires java.logging;
    requires org.jgrapht.core;
    requires org.jgrapht.ext;
    requires org.jgrapht.demo;
    opens donutnv.familytree.Controller to javafx.fxml;


}
