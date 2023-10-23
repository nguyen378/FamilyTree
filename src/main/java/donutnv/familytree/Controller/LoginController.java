/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package donutnv.familytree.Controller;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javax.swing.JOptionPane;

public class LoginController implements Initializable {

    private static final String URI = "bolt://localhost:7687";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "123456789";

    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private Button dangnhap;
    
    @FXML
    private TextField txtuser;
    
    @FXML
    private PasswordField txtpass;
    @FXML
    private void dangnhap(ActionEvent event) {
        String username = txtuser.getText();
        String password = txtpass.getText();
        if(username.isEmpty() || password.isEmpty())
        {
            showAlert("Cảnh báo", "Tài khoản và mật khẩu không được để trống", Alert.AlertType.ERROR);
        }
        else{
            if(username.equals(USER) && password.equals(PASSWORD))
            {
                showAlert("Thông báo", "Đăng nhập thành công", Alert.AlertType.INFORMATION);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/donutnv/familytree/home.fxml"));
                try {
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                   Node sourceNode = (Node) event.getSource();
                    Stage stage = (Stage) sourceNode.getScene().getWindow();
                    stage.close();
                    Stage newStage = new Stage();
                    newStage.setTitle("Home");
                    newStage.setScene(scene);
                    newStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                showAlert("Cảnh báo", "Tên đăng nhập hoặc mật khẩu không đúng", Alert.AlertType.ERROR);
            }
        }
    }
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
