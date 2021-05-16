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
            System.out.println("Tipo de Ficheiro:" + tipo_ficheiro);

            ///////////////////////////////
            if(tipo_ficheiro.equals("keys-and-iv.txt")){

                if (FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath())[1].equals("")) {

                    CipherUtil cipherUtil = verificarDesencriptar(selectedFile, selectedKeyFile);

                    if (cipherUtil != null) {
                        txtAreaTotal.setText(cipherUtil.getDecryptedString());
                        txtAreaTotal.requestFocus();
                    }

                }
                if (FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath())[0].equals("")) {

                    AuthenticateUtils authenticateUtils = verificarHmac(selectedFile, selectedKeyFile);

                    if (authenticateUtils != null) {

                        txtAreaTotal.setText(authenticateUtils.getInput());
                        txtAreaTotal.requestFocus();

                    } else {
                        System.out.println("Não é o mesmo cuidado!!!!!");
                    }

                }
                if (!FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath())[0].equals("") &&
                        !FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath())[1].equals("")&&
                        !FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath())[2].equals("")) {

                    AuthenticateUtils authenticateUtils = verificarHmac(selectedFile, selectedKeyFile);

                    if (authenticateUtils != null) {

                        CipherUtil cipherUtil = verificarDesencriptar(selectedFile, selectedKeyFile);
                        if (cipherUtil != null) {
                            txtAreaTotal.setText(cipherUtil.getDecryptedString());
                            txtAreaTotal.requestFocus();
                        } else {
                            System.out.println("Nao existe cifrado");
                        }

                    } else {
                        System.out.println("AVISO , NAO É O MESMO!!!!!");
                    }

                }

            }


        }
    }

    ///// GANDA CONFUSÃO !!!!!!!!!!!!!!!!!!!!!!!!
    @FXML
    void createFile(ActionEvent event) {
        //Se não foi aberto usando o Open
        String text = txtAreaTotal.getText();
        // Se o ficheiro ainda não existir
        if (currentFilePath.equals("")) {
            popupUtils.selectionPopup((Stage) txtAreaTotal.getScene().getWindow(), text);

        } else { //incompleto -> melhorar , verificar se o ficheiro foi só cifrado , autenticado ou ambos
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

    private AuthenticateUtils verificarHmac(File selectedFile, File selectedKeyFile) {

        currentFilePath = selectedFile.getAbsolutePath();
        String currentKeyPath = selectedKeyFile.getAbsolutePath();

        AuthenticateUtils authenticateUtils = new AuthenticateUtils(FileHandler.readFile(currentFilePath),
                FileHandler.readFileStringList(currentKeyPath)[1], FileHandler.readFileStringList(currentKeyPath)[2]);

        this.currentAuthenticateUtil = authenticateUtils;

        String texto = currentAuthenticateUtil.getInput();
        String hmac = currentAuthenticateUtil.getHmac();
        String privateKey = currentAuthenticateUtil.getPrivateKey();

        if (currentAuthenticateUtil.verifyHmac(texto, hmac, privateKey))
            return authenticateUtils;

        return null;

    }

    private CipherUtil verificarDesencriptar(File selectedFile, File selectedKeyFile) {

        currentFilePath = selectedFile.getAbsolutePath();
        String currentKeyPath = selectedKeyFile.getAbsolutePath();

        CipherUtil cipherUtil = new CipherUtil(FileHandler.readFile(currentFilePath),
                FileHandler.readFileStringList(currentKeyPath)[0]);
        currentCipherUtil = cipherUtil;

        return cipherUtil;

    }

    private String getFileType(File file) {
        if (file != null) {
            String nome = file.getName();
            String[] partes = nome.split("_");
            return partes[partes.length - 1];
        }
        return "";
    }

}