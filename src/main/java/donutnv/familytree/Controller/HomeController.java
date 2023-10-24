/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package donutnv.familytree.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import donutnv.familytree.FamilyTree;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 * FXML Controller class
 *
 * @author Thuong Nguyen
 */
public class HomeController implements Initializable {

    @FXML
    private Button btnShowFamilyTree;

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private Button buttonlv; 
    @FXML
    private void buttonliving(ActionEvent event)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/donutnv/familytree/livingat.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
           Node sourceNode = (Node) event.getSource();
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.close();
            Stage newStage = new Stage();
            newStage.setTitle("Living At");
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnShowFamilyTree.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                FamilyTree app = new FamilyTree();
                app.setSize(2000, 600);
                app.setLocationRelativeTo(null);
                app.setVisible(true);
            }

        });
    }
    // TODO

}
