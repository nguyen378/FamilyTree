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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;

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
        String cypherQuery = "MATCH (person:Information)-[rel]->(location:Location) " +
                "RETURN person.id AS personId, location.id AS locationId, type(rel) AS relationType";
//        if (!personid.isEmpty()) {
//            cypherQuery += "AND person.id = '" + personid + "' ";
//        }
//        if (!locationid.isEmpty()) {
//            cypherQuery += "AND location.id = '" + locationid + "' ";
//        }
//        if (!livingstt.isEmpty()) {
//            cypherQuery += "AND type(rel) = '" + livingstt + "' ";
//        }
//        cypherQuery += "RETURN person.id AS personId, location.id AS locationId, type(rel) AS relationType";

        org.neo4j.driver.Result result = session.run(cypherQuery);
        table1.getItems().clear();
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
        String personid = txt_person.getText();
        String locationid = txt_location.getText();

        // Kiểm tra nếu personid hoặc locationid rỗng, thì không thêm mối quan hệ
        if (personid.isEmpty() || locationid.isEmpty()) {
            showAlert("Cảnh báo", "PersonID và LocationID không được để trống.", Alert.AlertType.ERROR);
            return;
        }

        try (Session session = driver.session()) {
            // Kiểm tra sự tồn tại của node person và location
            String existenceQuery = "MATCH (person:Information {id: $personId}) RETURN person";
            String locationExistenceQuery = "MATCH (location:Location {id: $locationId}) RETURN location";
            int locationIdInt = Integer.parseInt(locationid);
            org.neo4j.driver.Result personExistenceResult = session.run(existenceQuery, Values.parameters("personId", personid));
            org.neo4j.driver.Result locationExistenceResult = session.run(locationExistenceQuery, Values.parameters("locationId", locationIdInt));

            if (personExistenceResult.hasNext() && locationExistenceResult.hasNext()) {
                // Person và Location tồn tại, thêm mối quan hệ
                String cypherQuery = "MATCH (person:Information {id: $personId}) " +
                                     "MATCH (location:Location {id: $locationId}) " +
                                     "CREATE (person)-[rel:Living_At]->(location) " +
                                     "RETURN person, rel, location";

                org.neo4j.driver.Result result = session.run(cypherQuery, Values.parameters("personId", personid, "locationId", locationIdInt));

                if (result.hasNext()) {
                    // Thêm mối quan hệ thành công
                    showAlert("Thông báo", "Thêm thành công.", Alert.AlertType.INFORMATION);
                } else {
                    // Thêm mối quan hệ không thành công
                    showAlert("Cảnh báo", "Thêm không thành công.", Alert.AlertType.ERROR);
                }
            } else {
                // Node person hoặc location không tồn tại
                showAlert("Cảnh báo", "Node person hoặc location không tồn tại.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Cảnh báo", "Lỗi khi thêm mối quan hệ.", Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void buttondel()
    {
        String personid = txt_person.getText();
        String locationid = txt_location.getText();

        // Kiểm tra nếu personid hoặc locationid rỗng, thì không thực hiện xóa
        if (personid.isEmpty() || locationid.isEmpty()) {
            showAlert("Cảnh báo", "PersonID và LocationID không được để trống.", Alert.AlertType.ERROR);
            return;
        }

        try (Session session = driver.session()) {
            // Tìm mối quan hệ để xóa
            String cypherQuery = "MATCH (person:Information {id: $personId})-[rel:Living_At]->(location:Location {id: $locationId}) DELETE rel";

            // Chuyển đổi locationid thành số nguyên
            int locationIdInt = Integer.parseInt(locationid);

            org.neo4j.driver.Result result = session.run(cypherQuery, Values.parameters("personId", personid, "locationId", locationIdInt));

            if (result.consume().counters().relationshipsDeleted() > 0) {
                // Xóa mối quan hệ thành công
                showAlert("Thông báo", "Xóa thành công.", Alert.AlertType.INFORMATION);
            } else {
                // Không có mối quan hệ để xóa
                showAlert("Cảnh báo", "Không tìm thấy mối quan hệ để xóa.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Cảnh báo","Lỗi khi xóa mối quan hệ.", Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void buttonedit()
    {
        String personid = txt_person.getText();
        String locationid = txt_location.getText();
        String livingstt = txt_living.getText();

        // Kiểm tra nếu personid, locationid hoặc livingstt rỗng, thì không thực hiện sửa
        if (personid.isEmpty() || locationid.isEmpty() || livingstt.isEmpty()) {
            System.out.print("PersonID, LocationID và Living Status không được để trống.");
            return;
        }

        try (Session session = driver.session()) {
            // Kiểm tra sự tồn tại của node person và location
            String existenceQuery = "MATCH (person:Information {id: $personId}) RETURN person";
            String locationExistenceQuery = "MATCH (location:Location {id: $locationId}) RETURN location";

            int locationIdInt = Integer.parseInt(locationid);
            org.neo4j.driver.Result personExistenceResult = session.run(existenceQuery, Values.parameters("personId", personid));
            org.neo4j.driver.Result locationExistenceResult = session.run(locationExistenceQuery, Values.parameters("locationId", locationIdInt));

            if (personExistenceResult.hasNext() && locationExistenceResult.hasNext()) {
                // Person và Location tồn tại, thực hiện sửa mối quan hệ
                String cypherQuery = "MATCH (person:Information {id: $personId}) " +
                                    "MATCH (location:Location {id: $locationId}) " +
                                    "MATCH (person)-[rel:Living_At]->(location) " +
                                    "SET person.id = $personId, location.id = $locationId, rel.type = $newLivingStatus " +
                                    "RETURN person, rel, location";

                org.neo4j.driver.Result result = session.run(cypherQuery, Values.parameters("personId", personid, "locationId", locationIdInt, "newLivingStatus", livingstt));

                if (result.hasNext()) {
                    // Sửa mối quan hệ thành công
                    System.out.print("Sửa thành công.");
                } else {
                    // Sửa mối quan hệ không thành công
                    System.out.print("Sửa không thành công.");
                }
            } else {
                // Node person hoặc location không tồn tại
                System.out.print("Node person hoặc location không tồn tại.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("Lỗi khi sửa mối quan hệ.");
        }
    }
    @FXML
    private void buttonfind()
    {
        
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    } 
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
}
