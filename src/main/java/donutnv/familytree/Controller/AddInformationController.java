/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package donutnv.familytree.Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author ACER
 */
public class AddInformationController implements Initializable {

    @FXML
    private  TextField txtID;
    @FXML 
    private TextField txtName;
    @FXML
    private ComboBox<String> cboSex;
    @FXML
    private TextField txtPhone;
    @FXML
    private DatePicker dpDateOfBirth;
    @FXML 
    private DatePicker dpDateOfDeath;
    @FXML
    private TextField txtPlace;
    @FXML
    private TextField txtEdu;
    @FXML
    private TextField txtMajor;
    @FXML
    private TextField txtNotes;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        cboSex.setItems(FXCollections.observableArrayList("Nam","Nữ"));
    }

}