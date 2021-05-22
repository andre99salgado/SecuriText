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
    
    //____________________________________________Texto do Help_________________
    private String textoHelp = "Damos-lhe as boas-vindas ao editor de texto seguro! \n"
            + "\n"
            + "\n"
            + "Novo Documento \n"
            + "\n"
            + "Selecionando a opção “New” abre uma nova janela do editor de texto.\n"
            + "\n"
            + "Abrir documentos \n"
            + "\n"
            + "Caso o que pretenda seja abrir um ficheiro selecione o item “Open”. Esta seleção faz com que aceda á pasta dos ficheiros criados, selecionando o ficheiro que deseja abrir, seguido do respetivo ficheiro de chaves.\n"
            + "\n"
            + "Guardar Documentos \n"
            + "\n"
            + "Guardar pela primeira-vez \n"
            + "\n"
            + "Para efetuar esta ação deve selecionar “Save” que desencadeia uma janela com três opções, “Encrypt”, “Authenticate” e “Both”. \n"
            + "	\n"
            + "Ao selecionar:\n"
            + "\n"
            + "•	“Encrypt” estará a guardar o ficheiro na sua forma cifrada.\n"
            + "•	“Authenticate” guardará o ficheiro na sua forma autenticada. \n"
            + "Ao selecionar esta opção irá ter acesso a uma janela na qual pode inserir o par de chaves pública e privada a usar. Se não tem nenhum par de chaves ou quer gerar um novo por qualquer motivo, basta clicar em “Generate Pair”.\n"
            + "Após isso, efetua a cópia das chaves a usar para a janela que lhe pede a inserção das mesmas e clica “OK”. Esta seleção desencadeia a janela onde tem à escolha duas formas de autenticação, “HMAC” ou “SIGN”, para autenticar o seu ficheiro basta selecionar uma e guardar o ficheiro. \n"
            + "		\n"
            + "•	“Both” irá guardar o ficheiro na sua forma cifrada autenticada.\n"
            + "Ao selecionar esta opção irá ter acesso a uma janela na qual pode inserir o par de chaves pública e privada a usar. Se não tem nenhum par de chaves ou quer gerar um novo por qualquer motivo, basta clicar em “Generate Pair”.\n"
            + "Após isso, efetua a copia das chaves a usar para a janela que lhe pede a inserção das mesmas e clica “OK”. Esta seleção desencadeia a janela onde tem à escolha duas formas de autenticação, “HMAC” ou “SIGN”, para autenticar e cifrar o seu ficheiro basta selecionar uma e guardar o ficheiro. \n"
            + "\n"
            + " \n"
            + "\n"
            + "\n"
            + "\n"
            + "\n"
            + "Guardar\n"
            + "\n"
            + "Após a abertura do ficheiro, este pode ser alterado e guardado novamente, bastando para isso aceder á opção “Save”. \n"
            + "\n"
            + "\n"
            + "Guardar Como \n"
            + "\n"
            + "Ao aceder ao “Save as” é direcionado para a janela com as três opções, “Encrypt”, “Authenticate” ou“Both”, descritas anteriormente (em Guardar pela primeira-vez). Deve escolher a que desejar, e o seu ficheiro será guardado com as preferências que selecionar. \n"
            + "\n"
            + "\n"
            + "Gerar par de chaves pública e privada\n"
            + "\n"
            + "Se pretender gerar um par de chaves publica e privada, basta aceder ao menu “Keys” e selecionar a opção “Generate Key Pair”.\n"
            + "\n"
            + "\n"
            + "Copiar e colar texto \n"
            + "\n"
            + "Para copiar ou colar texto basta carregar no botão direito do rato e tem acesso às respetivas opções.\n"
            + "\n"
            + "Sair \n"
            + "\n"
            + "Para terminar o uso da aplicação opta pela opção “Quit”.\n"
            + "\n"
            + "";
    //__________________________________________________________________________
    
    
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


                OperationType operation = Operations.chooseOperation(!fileStringList[0].equals(""), !fileStringList[1].equals(""),
                        !fileStringList[2].equals(""), !fileStringList[3].equals(""), !fileStringList[4].equals(""), !fileStringList[5].equals(""));
                if (operation == OperationType.NOTHING) return ;

                if (operation == OperationType.ENCRYPT_HMAC) {
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
                //System.out.println("Tipo de Ficheiro:" + tipo_ficheiro);

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
    
    
     @FXML
    void helpButtonControl(ActionEvent event) {
         popupUtils.PopupHelp((Stage) txtAreaTotal.getScene().getWindow(),textoHelp);
    }
    
    
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

    
    //métodos auxiliares para a verificação de Hmac e Assinatura_____________________
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

    //__________________________________________________________________________
   
    // Método para auxilio de desencriptação (na abertura do ficheiro)
    private CipherUtil verificarDesencriptar(File selectedFile, File selectedKeyFile) {

        currentFilePath = selectedFile.getAbsolutePath();
        String currentKeyPath = selectedKeyFile.getAbsolutePath();
        String input = FileHandler.readFile(currentFilePath);
        String[] keyValues = FileHandler.readFileStringList(currentKeyPath);
        CipherUtil cipherUtil = new CipherUtil(input, keyValues[0], CipherUtil.getStringAsIv(keyValues[3]));
        currentCipherUtil = cipherUtil;

        return cipherUtil;

    }

    //Método para o obter o tipo do ficheiro
    private String getFileType(File file) {
        if (file != null) {
            String nome = file.getName();
            String[] partes = nome.split("_");
            return partes[partes.length - 1];
        }
        return "";
    }
    
    

}