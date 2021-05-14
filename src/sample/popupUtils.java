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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class popupUtils {

    private static ArrayList<String> keys = new ArrayList<String>();
    /*
        keys.get(0) = encrypt key
        keys.get(1) = rsa private key
        keys.get(1) = mac
    */

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

    public static void selectionPopup(Stage currentStage, String text) {

        //action para encriptar o texto e guardar num file
        Button encrypt = new Button("Encrypt");
        encrypt.setOnAction(event -> {

            //encriptar o texto e guardar num file ----
            CipherUtil cipherUtil = new CipherUtil(text);
            File fileSaved = FileHandler.FileChooserAndSave(cipherUtil.getEncryptedString());
            //----------------------------

            //Guardar as chaves necessárias quando encriptamos o file
            //ArrayList<String> keys = new ArrayList<String>();
            keys.add(cipherUtil.getKeyAsString());
            FileHandler.writeFileArrayString(keys, Paths.get(fileSaved.getParent(), (fileSaved.getName() + "-key.txt")).toAbsolutePath().toString());
            //----------------------------

            //Fechar depois de clicar em algum botão
            CloseAndWarn(event);
        });

        //action para autenticar o texto e guardar num file
        Button authenticate = new Button("Authenticate");
        authenticate.setOnAction(event -> {

            try {
                //Autentica e guarda o texto -----
                AuthenticateUtils authenticateUtils = new AuthenticateUtils(text);
                File fileSaved = FileHandler.FileChooserAndSave(text); // ficheiro original
                //----------------------------

                //Guardar as chaves necessárias quando autenticamos o file -----
                System.out.println("\nEste é o HMAC:" + authenticateUtils.calculateHMAC(text));
                System.out.println("\n Esta é a private key " + authenticateUtils.getPrivateKey());
                //ArrayList<String> keys = new ArrayList<String>();
                keys.add(""); //key.get(0) -> chave de encrypt fica vazia porque só estamos a autenticar o file
                keys.add(authenticateUtils.getPrivateKey()); // add rsa private key para verificar o mac do file

                FileHandler.writeFileArrayString(keys, Paths.get(fileSaved.getParent(),
                        (fileSaved.getName() + "-keyHmac.txt")).toAbsolutePath().toString()); // ficheiro com chave privada

                FileHandler.writeFile(authenticateUtils.calculateHMAC(text), Paths.get(fileSaved.getParent(),
                        (fileSaved.getName() + "-hmac.txt")).toAbsolutePath().toString()); // ficheiro com o hmac
                //----------------------------

            } catch (SignatureException ex) {
                Logger.getLogger(popupUtils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(popupUtils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeyException ex) {
                Logger.getLogger(popupUtils.class.getName()).log(Level.SEVERE, null, ex);
            }


            CloseAndWarn(event);
        });

        Button both = new Button("Both");
        both.setOnAction(event -> {
            //FAZER
            // Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            //cifrar
            CipherUtil cipherUtil = new CipherUtil(text);
            String encriptada = cipherUtil.getEncryptedString();
            //FileHandler.writeFile(cipherUtil.getKeyAsString(), Paths.get(fileSaved.getParent(), (fileSaved.getName() + "-key.txt")).toAbsolutePath().toString());

            //autentiticar
            AuthenticateUtils authenticateUtils = new AuthenticateUtils(encriptada);

            try {

                File fileSaved = FileHandler.FileChooserAndSave(encriptada); // ficheiro encriptado
                ArrayList<String> keys = new ArrayList<String>();
                keys.add(cipherUtil.getKeyAsString());
                keys.add(authenticateUtils.getPrivateKey());
                FileHandler.writeFileArrayString(keys, Paths.get(fileSaved.getParent(), (fileSaved.getName() + "-EncryptKeyPrivateKey.txt")).toAbsolutePath().toString());
                //FileHandler.writeFile(authenticateUtils.getPrivateKey(), Paths.get(fileSaved.getParent(), (fileSaved.getName() + "-EncryptKeyPrivateKey.txt")).toAbsolutePath().toString());
                FileHandler.writeFile(authenticateUtils.calculateHMAC(encriptada), Paths.get(fileSaved.getParent(), (fileSaved.getName() + "-hmac.txt")).toAbsolutePath().toString()); // ficheiro com o hmac

            } catch (SignatureException ex) {
                Logger.getLogger(popupUtils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(popupUtils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeyException ex) {
                Logger.getLogger(popupUtils.class.getName()).log(Level.SEVERE, null, ex);
            }



            CloseAndWarn(event);
        });

        popup(currentStage, encrypt, authenticate, both);

    }

    private static void closeFromEvent(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    private static void CloseAndWarn(ActionEvent event) {
        Stage newStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        closeFromEvent(event);
        MessagePopup(newStage, "It's recommended that you remove the generated key file from the directory you saved to.");

    }

    private static void MessagePopup(Stage currentStage, String message) {
        Label label = new Label(message);
        Button okButton = new Button("OK");
        okButton.setOnAction(popupUtils::closeFromEvent);
        popup(currentStage, label, okButton);

    }


}