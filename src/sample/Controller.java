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
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Objects;
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
    private File selectedFile = null;
    private File selectedKeyFile = null;
    private static KeysUtils keyaux;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void openFile(ActionEvent event) {

        selectedFile = FileHandler.FileChooserAndGetFile();
        selectedKeyFile = FileHandler.FileChooserAndGetFile();

        //TODO: talvez adicionar alguma cena para a chave ser introduzida manualmente
        if (selectedFile != null && selectedKeyFile != null) {

            String tipo_ficheiro = getFileType(selectedKeyFile);

            ///////////////////////////////
            if(tipo_ficheiro.equals("keys-and-iv.txt")){

                if (Objects.requireNonNull(FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath()))[1].equals("")) {

                    CipherUtil cipherUtil = verificarDesencriptar(selectedFile, selectedKeyFile);

                    if (cipherUtil != null) {
                        txtAreaTotal.setText(cipherUtil.getDecryptedString());
                        txtAreaTotal.requestFocus();
                    }

                }
                if (Objects.requireNonNull(FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath()))[0].equals("")) {

                    AuthenticateUtils authenticateUtils = verificarHmac(selectedFile, selectedKeyFile);

                    if (authenticateUtils != null) {

                        txtAreaTotal.setText(authenticateUtils.getInput());
                        txtAreaTotal.requestFocus();

                    } else {
                        System.out.println("Não é o mesmo cuidado!!!!!");
                    }


                } else {
                    popupUtils.MessagePopup((Stage) txtAreaTotal.getScene().getWindow(), "Warning! Failed to " +
                            "Authenticate this file.");
                }
                if (!Objects.requireNonNull(FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath()))[0].equals("") &&
                        !Objects.requireNonNull(FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath()))[1].equals("")&&
                        !Objects.requireNonNull(FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath()))[2].equals("")) {

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
            if (selectedFile != null && selectedKeyFile != null){

                String tipo_ficheiro = getFileType(selectedKeyFile);
                System.out.println("Tipo de Ficheiro:" + tipo_ficheiro);

                if(tipo_ficheiro.equals("keys-and-iv.txt")){

                    if (Objects.requireNonNull(FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath()))[1].equals("")){
                        currentCipherUtil.setInput(text);
                        FileHandler.writeFile(currentCipherUtil.getEncryptedString(), currentFilePath);
                    }

                    if (Objects.requireNonNull(FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath()))[0].equals("")){
                        try {
                            keyaux = new KeysUtils("", currentAuthenticateUtil.getPrivateKey(), currentAuthenticateUtil.calculateHMAC(text), "");
                        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException e) {
                            e.printStackTrace();
                        }

                        FileHandler.writeFileArrayString(keyaux.getKeysF(), selectedKeyFile.getAbsolutePath()); // ficheiro com chave privada
                        FileHandler.writeFile(text, currentFilePath);
                    }

                    if(!Objects.requireNonNull(FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath()))[0].equals("") &&
                            !Objects.requireNonNull(FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath()))[1].equals("")&&
                            !Objects.requireNonNull(FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath()))[2].equals("")){

                        currentCipherUtil.setInput(text);
                        FileHandler.writeFile(currentCipherUtil.getEncryptedString(), currentFilePath);
                        try {
                            keyaux = new KeysUtils(currentCipherUtil.getKeyAsString(), currentAuthenticateUtil.getPrivateKey(), currentAuthenticateUtil.calculateHMAC(currentCipherUtil.getEncryptedString()), "");
                        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException e) {
                            e.printStackTrace();
                        }
                        FileHandler.writeFileArrayString(keyaux.getKeysF(), selectedKeyFile.getAbsolutePath()); // ficheiro com chave privada

                    }


                }





            }



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
    void generateKeyPair(ActionEvent event) {
         popupUtils.RSAKeys((Stage) txtAreaTotal.getScene().getWindow());
    }
    
    
    /*
    @FXML
    void setKeyPair(ActionEvent event,String text) {
         popupUtils.insertKeys((Stage) txtAreaTotal.getScene().getWindow(),text);
    }
    */
    
    
    @FXML
    void createFileSaveAs(ActionEvent event){
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

    //TODO: Mudar para o ficheiro CipherUtils
    private CipherUtil verificarDesencriptar(File selectedFile, File selectedKeyFile) {

        currentFilePath = selectedFile.getAbsolutePath();
        String currentKeyPath = selectedKeyFile.getAbsolutePath();

        CipherUtil cipherUtil = new CipherUtil(FileHandler.readFile(currentFilePath),
                FileHandler.readFileStringList(currentKeyPath)[0]);
        currentCipherUtil = cipherUtil;

        return cipherUtil;

    }

    //TODO: Mudar para o ficheiro FileHandler
    private String getFileType(File file) {
        if (file != null) {
            String nome = file.getName();
            String[] partes = nome.split("_");
            return partes[partes.length - 1];
        }
        return "";
    }

}