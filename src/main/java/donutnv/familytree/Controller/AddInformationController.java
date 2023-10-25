/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package donutnv.familytree.Controller;

import donutnv.familytree.App;
import donutnv.familytree.DataBase.ConnectDatbase;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.converter.StringConverter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

/**
 * FXML Controller class
 *
 * @author ACER
 */
public class AddInformationController implements Initializable {

    @FXML
    private TextField txtID;
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
    @FXML
    private Button btnAdd;
    @FXML
    private Button btndelete;
    
    private static Scene scene;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        cboSex.setItems(FXCollections.observableArrayList("Male", "Female"));
        String pattern = "yyyy-MM-dd";
        javafx.util.StringConverter converter = new javafx.util.StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };

        dpDateOfBirth.setConverter(converter);
        dpDateOfDeath.setConverter(converter);

        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                addInfo();
            }
        });
        btndelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("DeleteInfomation.fxml"));
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

    @FXML
    public void addInfo() {
        int id = Integer.parseInt(txtID.getText().toString());
        String name = txtName.getText().toString();
        String sex = cboSex.getSelectionModel().getSelectedItem();
        String phoneNumber = txtPhone.getText().toString();
        LocalDate dateOfBirth = dpDateOfBirth.getValue();
        LocalDate dateOfDeath = dpDateOfDeath.getValue();
        String place = txtPlace.getText().toString();
        String edu = txtEdu.getText().toString();
        String major = txtMajor.getText().toString();
        String notes = txtNotes.getText().toString();

        String query = "create (:Information{id:" + id + ",name:'" + name + "',sex:'" + sex + "',phoneNumber:'" + phoneNumber + "',dateOfBirth:date('" + dateOfBirth + "'),dateOfDeath:date('" + dateOfDeath + "'),placeOfBirth:'" + txtPlace + "',education:'" + txtEdu + "',major:'" + txtMajor + "',Notes:'" + txtNotes + "'})";
        System.err.println(query);
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
