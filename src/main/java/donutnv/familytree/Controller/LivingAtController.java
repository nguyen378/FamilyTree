/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package donutnv.familytree.Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.neo4j.driver.Driver;

/**
 *
 * @author Thuong Nguyen
 */
public class LivingAtController implements Initializable{

    @FXML
    private TableView table1;
    private static final String URI = "bolt://localhost:7687";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "123456789";
    private Driver driver;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
    }
    
}
