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
            String[] fileStringList = Objects.requireNonNull(FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath()));

            ///////////////////////////////
            if (tipo_ficheiro.equals("keys-and-iv.txt")) {
                //Se está encriptado e autenticado


                System.out.println("oii ");
                OperationType operation = Operations.chooseOperation(!fileStringList[0].equals(""), !fileStringList[1].equals(""),
                        !fileStringList[2].equals(""), !fileStringList[3].equals(""), !fileStringList[4].equals(""), !fileStringList[5].equals(""));
                if (operation == OperationType.NOTHING) return ;

                if (operation == OperationType.ENCRYPT_HMAC) {
                    System.out.println("veio aqui???");
                    AuthenticateUtils authenticateUtils = verificarHmac(selectedFile, selectedKeyFile);

                    if (authenticateUtils != null) {

                        CipherUtil cipherUtil = verificarDesencriptar(selectedFile, selectedKeyFile);
                        if (cipherUtil.getDecryptedString() != null) {
                            txtAreaTotal.setText(cipherUtil.getDecryptedString());
                            txtAreaTotal.requestFocus();
                        } else {
                            System.out.println("Nao existe cifrado");
                        }

                    } else {

                        popupUtils.MessagePopup((Stage) txtAreaTotal.getScene().getWindow(), "Warning! Encrypted" +
                                "Doesn't Exist!");
                    }
                    return;

                }

                //encrypt and signed
                if (operation == OperationType.ENCRYPT_SIGN) {

                    AuthenticateUtils authenticateUtils = verificarSignature(selectedFile, selectedKeyFile);

                    if (authenticateUtils != null) {

                        CipherUtil cipherUtil = verificarDesencriptar(selectedFile, selectedKeyFile);
                        if (cipherUtil.getDecryptedString() != null) {
                            txtAreaTotal.setText(cipherUtil.getDecryptedString());
                            txtAreaTotal.requestFocus();
                        } else {
                            System.out.println("Nao existe cifrado");
                        }

                    } else {

                        popupUtils.MessagePopup((Stage) txtAreaTotal.getScene().getWindow(), "Warning! Encrypted" +
                                "Doesn't Exist!");
                    }
                    return;

                }

                //Se está encriptado mas não está autenticado
                if (operation == OperationType.ENCRYPT) {

                    CipherUtil cipherUtil = verificarDesencriptar(selectedFile, selectedKeyFile);

                    if (cipherUtil.getDecryptedString() != null) {
                        txtAreaTotal.setText(cipherUtil.getDecryptedString());
                        txtAreaTotal.requestFocus();
                    } else {
                        popupUtils.MessagePopup((Stage) txtAreaTotal.getScene().getWindow(), "Warning! Failed to " +
                                "Decipher this file.");
                    }
                    return;

                }
                //Se está assinado mas não está cifrado
                if (operation == OperationType.SIGN) {

                    AuthenticateUtils authenticateUtils = verificarSignature(selectedFile, selectedKeyFile);
                            //verificarHmac(selectedFile, selectedKeyFile);

                    if (authenticateUtils != null) {

                        txtAreaTotal.setText(authenticateUtils.getInput());
                        txtAreaTotal.requestFocus();

                    } else {
                        popupUtils.MessagePopup((Stage) txtAreaTotal.getScene().getWindow(), "Warning! Failed to " +
                                "Authenticate this file.");
                    }
                    return;

                }


                //Se está autenticado mas não está encriptado
                if (operation == OperationType.HMAC) {

                    AuthenticateUtils authenticateUtils = verificarHmac(selectedFile, selectedKeyFile);

                    if (authenticateUtils != null) {

                        txtAreaTotal.setText(authenticateUtils.getInput());
                        txtAreaTotal.requestFocus();

                    } else {
                        popupUtils.MessagePopup((Stage) txtAreaTotal.getScene().getWindow(), "Warning! Failed to " +
                                "Authenticate this file.");
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
            if (selectedFile != null && selectedKeyFile != null) {

                String tipo_ficheiro = getFileType(selectedKeyFile);
                System.out.println("Tipo de Ficheiro:" + tipo_ficheiro);

                if (tipo_ficheiro.equals("keys-and-iv.txt")) {

                    if (Objects.requireNonNull(FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath()))[1].equals("")) {
                        currentCipherUtil.setInput(text);
                        FileHandler.writeFile(currentCipherUtil.getEncryptedString(), currentFilePath);
                    }

                    if (Objects.requireNonNull(FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath()))[0].equals("")) {
                        try {
                            keyaux = new KeysUtils("", currentAuthenticateUtil.getPrivateKey(), currentAuthenticateUtil.calculateHMAC(text), "");
                        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException e) {
                            e.printStackTrace();
                        }

                        FileHandler.writeFileArrayString(keyaux.getKeysF(), selectedKeyFile.getAbsolutePath()); // ficheiro com chave privada
                        FileHandler.writeFile(text, currentFilePath);
                    }

                    if (!Objects.requireNonNull(FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath()))[0].equals("") &&
                            !Objects.requireNonNull(FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath()))[1].equals("") &&
                            !Objects.requireNonNull(FileHandler.readFileStringList(selectedKeyFile.getAbsolutePath()))[2].equals("")) {

                        currentCipherUtil.setInput(text);
                        FileHandler.writeFile(currentCipherUtil.getEncryptedString(), currentFilePath);
                        try {
                            keyaux = new KeysUtils(currentCipherUtil.getKeyAsString(), currentAuthenticateUtil.getPrivateKey(), currentAuthenticateUtil.calculateHMAC(currentCipherUtil.getEncryptedString()), currentCipherUtil.getIvBytesAsString());
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

    private AuthenticateUtils verificarSignature(File selectedFile, File selectedKeyFile) {

        currentFilePath = selectedFile.getAbsolutePath();
        String currentKeyPath = selectedKeyFile.getAbsolutePath();

        AuthenticateUtils authenticateUtils = new AuthenticateUtils(FileHandler.readFile(currentFilePath),
                 Objects.requireNonNull(FileHandler.readFileStringList(currentKeyPath))[4],
                Objects.requireNonNull(FileHandler.readFileStringList(currentKeyPath))[5], "", "");

        this.currentAuthenticateUtil = authenticateUtils;

        String texto = currentAuthenticateUtil.getInput();
        String publicKey = currentAuthenticateUtil.getPublicKey();
        String signature = currentAuthenticateUtil.getSignedtext();

        try {
            if (currentAuthenticateUtil.verify(texto, signature, publicKey))
                return authenticateUtils;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


    //TODO: Mudar para o ficheiro CipherUtils
    private CipherUtil verificarDesencriptar(File selectedFile, File selectedKeyFile) {

        currentFilePath = selectedFile.getAbsolutePath();
        String currentKeyPath = selectedKeyFile.getAbsolutePath();
        String input = FileHandler.readFile(currentFilePath);
        String[] keyValues = FileHandler.readFileStringList(currentKeyPath);
        CipherUtil cipherUtil = new CipherUtil(input, keyValues[0], CipherUtil.getStringAsIv(keyValues[3]));
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