/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package donutnv.familytree.Controller;

import donutnv.familytree.App;
import donutnv.familytree.DataBase.ConnectDatbase;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

/**
 * FXML Controller class
 *
 * @author Thuong Nguyen
 */
public class LocationController implements Initializable {

    @FXML
    private TextField txtID;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextField txtState;
    @FXML
    private TextField txtCountry;
    @FXML
    private TextField txtType;
    @FXML
    private DatePicker dpStart;
    @FXML
    private DatePicker dpEnd;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnEdit;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        btnAdd.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                addLocation();
            }
        });
        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

                FXMLLoader loader = new FXMLLoader(App.class.getResource("DeleteLocation.fxml"));
                Parent root;
                try {
                    root = loader.load();
                    Stage newStage = new Stage();
                    newStage.setTitle("Delete");
                    newStage.setScene(new Scene(root));
                    newStage.show();
                } catch (IOException ex) {
                    Logger.getLogger(AddInformationController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        );
    }

    public void addLocation() {
        String query = "create (:Location{id:, address:'" + txtAddress.getText().toString() + "', country:'" + txtCountry.getText().toString() + "', start_date:date('" + dpStart.getValue() + "'), state:'" + txtState.getText().toString() + "', type:'" + txtType.getText().toString() + "'})";
        try (Driver driver = ConnectDatbase.createDriver()) {
            Session session = driver.session();
            try {
                session.run(query);
                JOptionPane.showMessageDialog(null, "Addition successful!", "Success", 1);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Addition failed!", "Error", 1);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Connection error!", "Error", 1);
        }
    }

}
