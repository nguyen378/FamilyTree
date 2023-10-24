/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package donutnv.familytree.Controller;

import donutnv.familytree.DataBase.Person;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;

/**
 * FXML Controller class
 *
 * @author Thuong Nguyen
 */
public class LivingatController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TableView table1;
    private static final String URI = "bolt://localhost:7687";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "123456789";
    private Driver driver;
    public LivingatController()
    {
        driver = GraphDatabase.driver(URI, AuthTokens.basic(USER, PASSWORD));
    }
    @FXML
    private Button btnshow;
    @FXML
    private Button btnadd;
    @FXML
    private Button btndel;
    @FXML
    private Button btnedit;
    @FXML
    private Button btnfind;
    @FXML
    private TextField txt_person;
    @FXML
    private TextField txt_location;
    @FXML
    private TextField txt_living;
    @FXML
    private void buttonshow()
    {
        String personid = txt_person.getText();
        String locationid = txt_location.getText();
        String livingstt = txt_living.getText();
        try (Session session = driver.session()) {
        // Xây dựng truy vấn Cypher
        String cypherQuery = "MATCH (person:Information)-[rel]->(location:Location)";
        
        if (!personid.isEmpty()) {
            cypherQuery += "AND person.id = '" + personid + "' ";
        }
        if (!locationid.isEmpty()) {
            cypherQuery += "AND location.id = '" + locationid + "' ";
        }
        if (!livingstt.isEmpty()) {
            cypherQuery += "AND rel = '" + livingstt + "' ";
        }
        cypherQuery += "RETURN person.id AS personId, location.id AS locationId, rel AS relationType";

        org.neo4j.driver.Result result = session.run(cypherQuery);

        // Xóa dữ liệu cũ trong TableView
        table1.getItems().clear();

        // Điền dữ liệu từ kết quả truy vấn vào TableView
        while (result.hasNext()) {
            org.neo4j.driver.Record record = result.next();
            Value personIdValue = record.get("person.id");
            int personIdIntValue = (personIdValue.isNull()) ? 0 : personIdValue.asInt();
            String locationIdValue = Integer.toString(record.get("locationId").asInt());
            String livingStatusValue = record.get("relationType").asString();
            String personIdStringValue = Integer.toString(personIdIntValue);
            table1.getItems().add(new Person(personIdStringValue, locationIdValue, livingStatusValue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void buttonadd()
    {
        
    }
    @FXML
    private void buttondel()
    {
        
    }
    @FXML
    private void buttonedit()
    {
        
    }
    @FXML
    private void buttonfind()
    {
        
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
