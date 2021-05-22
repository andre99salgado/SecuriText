package sample;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextArea;

public class popupUtils {

    private static KeysUtils keyaux;
    


    // Função geradora de popups
    // aceita como parametro de entrada o stage atual e uma lista de Nodes (botões, texto, etc)
    public static void popup(Stage currentStage, Node... es) {

        currentStage.setTitle("Popup Example");
        final Popup popup = new Popup();
        popup.setX(currentStage.getX());
        popup.setY(currentStage.getY());
        popup.getContent().addAll(new HBox());


        HBox layout = new HBox(10);
        layout.setStyle("-fx-padding: 10;");
        layout.getChildren().addAll(es);
        Stage stage = new Stage();
        stage.setScene(new Scene(layout));
        stage.show();
    }



    // Popup de seleção de operação [Encrypt, Authenticate, Both]
    public static void selectionPopup(Stage currentStage, String text) {

        //action para encriptar o texto e guardar num file
        Button encrypt = new Button("Encrypt");
        encrypt.setOnAction(event -> {

            //encriptar o texto e guardar num file ----
            CipherUtil cipherUtil = new CipherUtil(text);
            File fileSaved = FileHandler.FileChooserAndSave(cipherUtil.getEncryptedString());
            //----------------------------

            //Guardar as chaves necessárias quando encriptamos o file
            keyaux = new KeysUtils(cipherUtil.getKeyAsString(), "", "", cipherUtil.getIvBytesAsString());
            FileHandler.writeFileArrayString(keyaux.getKeysF(), Paths.get(fileSaved.getParent(), (getFileType(fileSaved.getName()) + "_keys-and-iv.txt")).toAbsolutePath().toString());
            //----------------------------

            //Fechar depois de clicar em algum botão
            CloseAndWarn(event);
        });

        //action para autenticar o texto e guardar num file
        Button authenticate = new Button("Authenticate");
        authenticate.setOnAction(event -> {
            //autentiticar
            insertKeys(currentStage,text, "","");
            CloseAndWarn(event);

        });

        Button both = new Button("Both");
        both.setOnAction(event -> {
            //cifrar
            CipherUtil cipherUtil = new CipherUtil(text);
            String encriptada = cipherUtil.getEncryptedString();
            //FileHandler.writeFile(cipherUtil.getKeyAsString(), Paths.get(fileSaved.getParent(), (fileSaved.getName() + "-key.txt")).toAbsolutePath().toString());

            //autentiticar
            insertKeys(currentStage,encriptada, cipherUtil.getIvBytesAsString(),cipherUtil.getKeyAsString());
            CloseAndWarn(event);

            /*AuthenticateUtils authenticateUtils = new AuthenticateUtils(encriptada);
            try {
                File fileSaved = FileHandler.FileChooserAndSave(encriptada); // ficheiro encriptado
                keyaux = new KeysUtils(cipherUtil.getKeyAsString(), authenticateUtils.getPrivateKey(), authenticateUtils.calculateHMAC(encriptada), cipherUtil.getIvBytesAsString());
                assert fileSaved != null;
                FileHandler.writeFileArrayString(keyaux.getKeysF(), Paths.get(fileSaved.getParent(), (getFileType(fileSaved.getName()) + "_keys-and-iv.txt")).toAbsolutePath().toString());
            } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException ex) {
                Logger.getLogger(popupUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
            CloseAndWarn(event); */
        });

        popup(currentStage, encrypt, authenticate, both);

    }

    // Função para fechar popup
    private static void closeFromEvent(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    // Função que avisa o utilizador a remover o ficheiro gerado da pasta onde foi gravada com um botão para fechar
    private static void CloseAndWarn(ActionEvent event) {
        Stage newStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        closeFromEvent(event);
        MessagePopup(newStage, "It's recommended that you remove the generated key file from the directory you saved to.");

    }

    // Função de Popup que apresenta uma mensagem (parâmetro de entrada) e um botão "OK" que fecha o Popup
    public static void MessagePopup(Stage currentStage, String message) {
        Label label = new Label(message);
        Button okButton = new Button("OK");
        okButton.setOnAction(popupUtils::closeFromEvent);
        popup(currentStage, label, okButton);

    }
    
    // Função de Popup que apresenta o texto Help
    public static void PopupHelp(Stage currentStage, String texto) {
        TextArea ta = new TextArea(texto);
        ta.setEditable(false);
        Button okButton = new Button("OK");   
        okButton.setOnAction(popupUtils::closeFromEvent);
        popup(currentStage, ta, okButton);

    }
    

    
    // Popup no qual é possível abrir outro popup para gerar pares de chaves  
    public static void RSAKeys(Stage currentStage) {
        Label label = new Label("Do you wish to Generate a public and a private key?");
        Button okButton = new Button("YES");
        okButton.setOnAction((event) -> {
            ShowKeys(event);
            closeFromEvent(event);
        });
        Button noButton = new Button("NO");
        noButton.setOnAction(popupUtils::closeFromEvent);
        popup(currentStage, label, okButton, noButton);
    }
    
    // Popup de entrada do par de chaves
     public static void insertKeys(Stage currentStage, String text, String iv, String encrypt) {
        Label labelS = new Label("Insert your private key");
        TextArea insertS = new TextArea();
        Label labelP = new Label("Insert your public key");
        TextArea insertP = new TextArea();
        Button okButton = new Button("OK");
        
       insertS.setPrefColumnCount(3);
       insertP.setPrefColumnCount(3);
       insertS.setPrefRowCount(1);
       insertP.setPrefRowCount(1);
        

        okButton.setOnAction((event) -> {
            String privateString = insertS.getText();
            String publicString = insertP.getText();
            HmacOrSign(currentStage,text,privateString,publicString, iv, encrypt);
            closeFromEvent(event);
        });
        Button generateButton = new Button("Generate Keys");
        generateButton.setOnAction((event) -> {
            ShowKeys(event);
        });
        
        popup(currentStage, labelS,insertS, labelP, insertP, okButton, generateButton);
    }
    
     
     
    // Popup de decisão entre HMAC e Signing do ficheiro a ser gravado
      public static void HmacOrSign(Stage currentStage, String text,String privateString, String publicString, String iv, String encrypt) {
        Label label = new Label("Authenticate With Hmac or Sign");
        Button hmac = new Button("HMAC");
        hmac.setOnAction((event) -> {
             try {
                //Autentica e guarda o texto -----
                AuthenticateUtils authenticateUtils = new AuthenticateUtils("",text,privateString,publicString);

                File fileSaved = FileHandler.FileChooserAndSave(text); // ficheiro original
                //----------------------------
                //Guardar as chaves necessárias quando autenticamos o file -----
                keyaux = new KeysUtils(encrypt, authenticateUtils.getPrivateKey(), authenticateUtils.calculateHMAC(text), iv);
                FileHandler.writeFileArrayString(keyaux.getKeysF(), Paths.get(fileSaved.getParent(),
                        (getFileType(fileSaved.getName()) + "_keys-and-iv.txt")).toAbsolutePath().toString()); // ficheiro com chave privada
                //----------------------------

            } catch (SignatureException | InvalidKeyException | NoSuchAlgorithmException ex) {
                Logger.getLogger(popupUtils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeySpecException ex) {
                Logger.getLogger(popupUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
            closeFromEvent(event);
        });
        
        Button sign = new Button("SIGN");
        sign.setOnAction((event) -> {
            
               AuthenticateUtils authenticateUtils;
            try {
                authenticateUtils = new AuthenticateUtils("",text,privateString,publicString);

               //_______________________________________________________________
               
               File fileSaved = FileHandler.FileChooserAndSave(text); // ficheiro original
                //----------------------------
                //Guardar as chaves necessárias quando autenticamos o file -----
                keyaux = new KeysUtils(encrypt, "", "", iv, authenticateUtils.getPublicKey(), authenticateUtils.getSignedText());
                FileHandler.writeFileArrayString(keyaux.getKeysF(), Paths.get(fileSaved.getParent(),
                        (getFileType(fileSaved.getName()) + "_keys-and-iv.txt")).toAbsolutePath().toString()); // ficheiro com chave privada
               
                
                } catch (InvalidKeySpecException ex) {
                Logger.getLogger(popupUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
               //_______________________________________________________________
            
            closeFromEvent(event);
        });
        popup(currentStage, label, hmac, sign);
    }
     
    
    
    
    // Popup que mostra as chaves
    public static void ShowKeys(ActionEvent event) {
        
       AuthenticateUtils authentic = new AuthenticateUtils();
       authentic.getPrivateKey();
       authentic.getPublicKey();
       //String public =  authentic.getPublicKey();
       
       
       Label label1 = new Label ("Private Key");
       TextArea s = new TextArea(authentic.getPrivateKey());
       Label label2 = new Label ("Public Key");
       TextArea p = new TextArea(authentic.getPublicKey());
       
       s.setEditable(false);
       p.setEditable(false);
       s.setPrefColumnCount(3);
       p.setPrefColumnCount(3);
       s.setPrefRowCount(1);
       p.setPrefRowCount(1);

       
       Button close = new Button("Close");
       close.setOnAction(popupUtils::closeFromEvent);

       Node source = (Node) event.getSource();
       Stage stage = (Stage) source.getScene().getWindow();
       popup(stage,label1,s,label2,p,close);
    }
    
    
    

    // Função que devolve a extensão do ficheiro
    private static String getFileType(String nome) {
        if (nome != null) {
            String[] partes = nome.split(getExtensionByStringHandling(nome).get());
            return partes[0].substring(0, partes[0].length() - 1);
        }
        return "";
    }

    public static Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

}
