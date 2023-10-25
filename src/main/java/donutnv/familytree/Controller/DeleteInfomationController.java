/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package donutnv.familytree.Controller;

import donutnv.familytree.DataBase.ConnectDatbase;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javax.swing.JOptionPane;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

/**
 * FXML Controller class
 *
 * @author ACER
 */
public class DeleteInfomationController implements Initializable {

    @FXML
    private ComboBox cboID;
    @FXML
    private TextField txtName;
    @FXML
    private Button btnDelete;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadID();
        loadCbo();
        deleteInfo();
    }

    public void deleteInfo() {
        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                int id = Integer.parseInt(cboID.getValue().toString());
                String query = "match(n:Information) where n.id = " + id + " detach delete n";
                try (Driver driver = ConnectDatbase.createDriver()) {
                    Session session = driver.session();
                    try {
                        session.run(query);
                        JOptionPane.showMessageDialog(null, "Delete successful!", "Success", 1);
                        loadCbo();
                        txtName.setText("");
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Delete failed!", "Error", 1);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Connection error!", "Error", 1);
                }
            }
        });
    }

    public void loadCbo() {
        cboID.getItems().clear();
        cboID.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                String query = "match(n:Information) where n.id = " + Integer.parseInt(t1) + " return n.name as name";
                try (Driver driver = ConnectDatbase.createDriver()) {
                    Session session = driver.session();
                    Result result = session.run(query);
                    org.neo4j.driver.Record record = result.next();
                    txtName.setText(record.get("name").toString());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Connection error!", "Error", 1);
                }
            }
        });
    }

    public void loadID() {
        String query = "match (n:Information) return n.id as id";
        List<String> listID = new ArrayList<>();
        try (Driver driver = ConnectDatbase.createDriver()) {
            Session session = driver.session();
            Result result = session.run(query);
            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next();
                listID.add(record.get("id").toString());
            }
            cboID.setItems(FXCollections.observableArrayList(listID));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Connection error!", "Error", 1);
        }
    }
    


}
