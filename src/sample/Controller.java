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

    private AuthenticateUtils currentAuthenticateUtil;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void openFile(ActionEvent event) {

        File selectedFile = FileHandler.FileChooserAndGetFile();
        File selectedKeyFile = FileHandler.FileChooserAndGetFile();

        //TODO: talvez adicionar alguma cena para a chave ser introduzida manualmente
        if (selectedFile != null && selectedKeyFile != null) {

            String tipo_ficheiro = getFileType(selectedKeyFile);

            ///////////////////////////////
            if (tipo_ficheiro.equals("key.txt")) {

                CipherUtil cipherUtil = verificarDesencriptar(selectedFile, selectedKeyFile);

                if (cipherUtil != null) {
                    txtAreaTotal.setText(cipherUtil.getDecryptedString());
                    txtAreaTotal.requestFocus();
                }

            }
            if (tipo_ficheiro.equals("keyHmac.txt")) {

                AuthenticateUtils authenticateUtils = verificarHmac(selectedFile, selectedKeyFile);

                if (authenticateUtils != null) {

                    txtAreaTotal.setText(authenticateUtils.getInput());
                    txtAreaTotal.requestFocus();

                } else {
                    popupUtils.MessagePopup((Stage) txtAreaTotal.getScene().getWindow(), "Warning! Failed to " +
                            "Authenticate this file.");
                }

            }
            if (tipo_ficheiro.equals("EncryptKeyPrivateKey.txt")) {

                AuthenticateUtils authenticateUtils = verificarHmac(selectedFile, selectedKeyFile);

                if (authenticateUtils != null) {

                    CipherUtil cipherUtil = verificarDesencriptar(selectedFile, selectedKeyFile);
                    if (cipherUtil != null) {
                        txtAreaTotal.setText(cipherUtil.getDecryptedString());
                        txtAreaTotal.requestFocus();
                    } else {
                        popupUtils.MessagePopup((Stage) txtAreaTotal.getScene().getWindow(), "Warning! Encrypted" +
                                "Doesn't Exist!");
                    }

                } else {
                    popupUtils.MessagePopup((Stage) txtAreaTotal.getScene().getWindow(), "Warning! Failed to " +
                            "Authenticate this file.");
                }

            }
        }
    }

    /*
        Button "Save"


        Verifica se algum ficheiro já foi aberto (currentFilePath.equals(""))
        Caso o ficheiro ainda não foi aberto: abre um PopUp para se escolher as operações a fazer no ficheiro (encrypt, auth, both)
        Caso contrário: Grava o ficheiro com a mesma chave mas com conteúdo diferente
     */
    @FXML
    void saveButtonControl(ActionEvent event) {
        String text = txtAreaTotal.getText();

        if (currentFilePath.equals("")) {
            popupUtils.selectionPopup((Stage) txtAreaTotal.getScene().getWindow(), text);

        } else { //incompleto -> melhorar , verificar se o ficheiro foi só cifrado , autenticado ou ambos
            currentCipherUtil.setInput(text);
            FileHandler.writeFile(currentCipherUtil.getEncryptedString(), currentFilePath);
        }

    }

    /*
        Button "Save As"
        abre um PopUp para se escolher as operações a fazer no ficheiro (encrypt, auth, both)
     */
    @FXML
    void saveAsButtonControl(ActionEvent event) {
        String text = txtAreaTotal.getText();
        popupUtils.selectionPopup((Stage) txtAreaTotal.getScene().getWindow(), text);
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

    //TODO: Mudar para o ficheiro AuthenticateUtils
    private AuthenticateUtils verificarHmac(File selectedFile, File selectedKeyFile) {

        File selectedHMACFile = FileHandler.FileChooserAndGetFile();

        currentFilePath = selectedFile.getAbsolutePath();
        String currentKeyPath = selectedKeyFile.getAbsolutePath();

        String currentHMACPath = selectedHMACFile.getAbsolutePath();

        AuthenticateUtils authenticateUtils = new AuthenticateUtils(FileHandler.readFile(currentFilePath),
                FileHandler.readFileStringList(currentKeyPath).get(1), FileHandler.readFile(currentHMACPath));

        this.currentAuthenticateUtil = authenticateUtils;

        String texto = currentAuthenticateUtil.getInput();
        String hmac = currentAuthenticateUtil.getHmac();
        String privateKey = currentAuthenticateUtil.getPrivateKey();

        if (currentAuthenticateUtil.verifyHmac(texto, hmac, privateKey))
            return authenticateUtils;

        return null;

    }

    //TODO: Mudar para o ficheiro CipherUtils
    private CipherUtil verificarDesencriptar(File selectedFile, File selectedKeyFile) {

        currentFilePath = selectedFile.getAbsolutePath();
        String currentKeyPath = selectedKeyFile.getAbsolutePath();

        CipherUtil cipherUtil = new CipherUtil(FileHandler.readFile(currentFilePath),
                FileHandler.readFileStringList(currentKeyPath).get(0));
        currentCipherUtil = cipherUtil;

        return cipherUtil;

    }

    //TODO: Mudar para o ficheiro FileHandler
    private String getFileType(File file) {
        if (file != null) {
            String nome = file.getName();
            String[] partes = nome.split("-");
            return partes[partes.length - 1];
        }
        return "";
    }

}