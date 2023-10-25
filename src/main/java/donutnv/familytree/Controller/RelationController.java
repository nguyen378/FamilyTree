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
 * @author Thuong Nguyen
 */
public class RelationController implements Initializable {

    @FXML
    private ComboBox cboPs1ID;
    @FXML
    private TextField txtPs1Name;
    @FXML
    private ComboBox cboPs2ID;
    @FXML
    private TextField txtPs2Name;
    @FXML
    private ComboBox cboRelation;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnDel;
    @FXML
    private Button btnUpdate;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadPs1ID();
        loadPs1Name();
        loadPs2ID();
        loadPs2Name();
        loadRelation();
        addRelation();
        deleteRelation();
        updateRelation();
    }

    public void loadPs1ID() {
        String query = "match (n:Information) return n.id as id";
        List<String> listID = new ArrayList<>();
        try (Driver driver = ConnectDatbase.createDriver()) {
            Session session = driver.session();
            Result result = session.run(query);
            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next();
                listID.add(record.get("id").toString());
            }
            cboPs1ID.setItems(FXCollections.observableArrayList(listID));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Connection error!", "Error", 1);
        }
    }

    public void loadPs1Name() {
        cboPs1ID.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                String query = "match(n:Information) where n.id = " + Integer.parseInt(t1) + " return n.name as name";
                try (Driver driver = ConnectDatbase.createDriver()) {
                    Session session = driver.session();
                    Result result = session.run(query);
                    org.neo4j.driver.Record record = result.next();
                    txtPs1Name.setText(record.get("name").toString());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Connection error!", "Error", 1);
                }
            }
        });
    }

    public void loadPs2ID() {
        String query = "match (n:Information) return n.id as id";
        List<String> listID = new ArrayList<>();
        try (Driver driver = ConnectDatbase.createDriver()) {
            Session session = driver.session();
            Result result = session.run(query);
            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next();
                listID.add(record.get("id").toString());
            }
            cboPs2ID.setItems(FXCollections.observableArrayList(listID));
//            cboPs1ID.valueProperty().addListener(new ChangeListener<String>() {
//                @Override
//                public void changed(ObservableValue<? extends String> ov, String t, String t1) {
//                    
//                }
//            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Connection error!", "Error", 1);
        }
    }

    public void loadPs2Name() {
        cboPs2ID.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                String query = "match(n:Information) where n.id = " + Integer.parseInt(t1) + " return n.name as name";
                try (Driver driver = ConnectDatbase.createDriver()) {
                    Session session = driver.session();
                    Result result = session.run(query);
                    org.neo4j.driver.Record record = result.next();
                    txtPs2Name.setText(record.get("name").toString());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Connection error!", "Error", 1);
                }
            }
        });
    }

    public void loadRelation() {
        String query = "match(:Information)-[r:Has_Relation]-(:Information) return distinct r.relation as relation";
        List<String> listID = new ArrayList<>();
        try (Driver driver = ConnectDatbase.createDriver()) {
            Session session = driver.session();
            Result result = session.run(query);
            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next();
                listID.add(record.get("relation").toString());
            }
            cboRelation.setItems(FXCollections.observableArrayList(listID));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Connection error!", "Error", 1);
        }
    }

    public void addRelation() {
        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                int id1 = Integer.parseInt(cboPs1ID.getValue().toString());
                int id2 = Integer.parseInt(cboPs2ID.getValue().toString());
                String rela = cboRelation.getValue().toString();
                String query = "match (p1:Information{id :" + id1 + "}),(p2:Information{id: " + id2 + "}) create (p1)-[:Has_Relation{relation:'" + rela + "'}]->(p2)  ";
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
        });
    }

    public void deleteRelation() {
        btnDel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                int id1 = Integer.parseInt(cboPs1ID.getValue().toString());
                int id2 = Integer.parseInt(cboPs2ID.getValue().toString());
                String query = "match (p1:Information{id :" + id1 + "})-[r:Has_Relation]->(p2:Information{id: " + id2 + " }) delete r ";
                try (Driver driver = ConnectDatbase.createDriver()) {
                    Session session = driver.session();
                    try {
                        session.run(query);
                        JOptionPane.showMessageDialog(null, "Delete successful!", "Success", 1);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Delete failed!", "Error", 1);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Connection error!", "Error", 1);
                }
            }
        });
    }

    public void updateRelation() {
        btnUpdate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                int id1 = Integer.parseInt(cboPs1ID.getValue().toString());
                int id2 = Integer.parseInt(cboPs2ID.getValue().toString());
                String rela = cboRelation.getValue().toString();
                String query = "match (p1:Information{id :" + id1 + "})-[r:Has_Relation]->(p2:Information{id: " + id2 + " }) set r.relation = '"+rela+"' ";
                try (Driver driver = ConnectDatbase.createDriver()) {
                    Session session = driver.session();
                    try {
                        session.run(query);
                        JOptionPane.showMessageDialog(null, "Update successful!", "Success", 1);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Update failed!", "Error", 1);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Connection error!", "Error", 1);
                }
            }
        });
    }
}
