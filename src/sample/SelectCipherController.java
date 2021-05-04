package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class SelectCipherController implements Initializable {

    private String text;

    @FXML
    private Button authenticateButton;

    @FXML
    private Button encryptButton;

    @FXML
    private Button bothButton;

    //TODO: ver outra forma de passar texto
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Esperar que o outro controlador mude o texto
        Platform.runLater(() -> {
            System.out.println(text);
        });
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @FXML
    public void authenticateClick(ActionEvent event) {
    }

    @FXML
    public void encryptClick(ActionEvent event) {
    }

    @FXML
    public void bothClick(ActionEvent event) {
    }
}
