package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Controller implements Initializable {


    @FXML
    private TextField txtArea;

    @FXML
    private TextArea txtAreaTotal;

    @FXML
    private VBox anchorid;

    private String currentFilePath = "";
    private CipherUtil currentCipherUtil;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void openFile(ActionEvent event) {

        File selectedFile = FileHandler.FileChooserAndGetFile();
        File selectedKeyFile = FileHandler.FileChooserAndGetFile();
        //TODO: talvez adicionar alguma cena para a chave ser introduzida manualmente
        if (selectedFile != null && selectedKeyFile != null) {

            currentFilePath = selectedFile.getAbsolutePath();
            String currentKeyPath = selectedKeyFile.getAbsolutePath();
            // Assume-se que, caso não haja o ficheiro de chaves que

            System.out.println(FileHandler.readFile(currentFilePath) + FileHandler.readFile(currentKeyPath));

            //txtArea.setText(selectedFile.getName());
            CipherUtil cipherUtil = new CipherUtil(FileHandler.readFile(currentFilePath),
                    FileHandler.readFile(currentKeyPath));
            this.currentCipherUtil = cipherUtil;

            txtAreaTotal.setText(cipherUtil.getDecryptedString());
            txtAreaTotal.requestFocus();
        }
    }

    @FXML
    void createFile(ActionEvent event) {
        //Se não foi aberto usando o Open
        String text = txtAreaTotal.getText();
        if (currentFilePath.equals("")) {
            popupUtils.selectionPopup((Stage) txtAreaTotal.getScene().getWindow(), text);

        } else {
            currentCipherUtil.setInput(text);
            FileHandler.writeFile(currentCipherUtil.getEncryptedString(), currentFilePath);
        }

    }


    @FXML
    public void newWindow(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("sample.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 300, 275);
            Stage stage = new Stage();
            stage.setTitle("New Window");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    @FXML
    private void closeButtonAction(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

}
