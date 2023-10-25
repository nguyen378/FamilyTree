/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package donutnv.familytree.Controller;

import donutnv.familytree.App;
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
import donutnv.familytree.Information;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javax.swing.JFrame;

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
    private Button buttonrela;
    @FXML
    private Button buttoninfor;
    @FXML
    private Button buttonlocat;

    @FXML
    private void buttonliving(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("Livingat.fxml"));
        Parent root;
        try {
            root = loader.load();
            Stage newStage = new Stage();
            newStage.setTitle("Living at");
            newStage.setScene(new Scene(root));
            newStage.show();
        } catch (IOException ex) {
            Logger.getLogger(AddInformationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void buttonrelation(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("relation.fxml"));
        Parent root;
        try {
            root = loader.load();
            Stage newStage = new Stage();
            newStage.setTitle("Relation");
            newStage.setScene(new Scene(root));
            newStage.show();
        } catch (IOException ex) {
            Logger.getLogger(AddInformationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void buttoninformation(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("AddInformation.fxml"));
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

    @FXML
    private void buttonlocation(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("location.fxml"));
        Parent root;
        try {
            root = loader.load();
            Stage newStage = new Stage();
            newStage.setTitle("Location");
            newStage.setScene(new Scene(root));
            newStage.show();
        } catch (IOException ex) {
            Logger.getLogger(AddInformationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void buttonshow(ActionEvent event) {
        FamilyTree familyTree = new FamilyTree();
        familyTree.setVisible(true);
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
}
