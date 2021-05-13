package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class popupUtils {


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
        Button encrypt = new Button("Encrypt");
        encrypt.setOnAction(event -> {
            CipherUtil cipherUtil = new CipherUtil(text);
            File fileSaved = FileHandler.FileChooserAndSave(cipherUtil.getEncryptedString());
            FileHandler.writeFile(cipherUtil.getKeyAsString(), Paths.get(fileSaved.getParent(), (fileSaved.getName() + "-key.txt")).toAbsolutePath().toString());
            //TODO: Deve mostrar outro POPUP a dizer para remover o ficheiro daquele sítio
            //Fechar depois de clicar em algum botão
            CloseAndWarn(event);
        });

        Button authenticate = new Button("Authenticate");
        authenticate.setOnAction(event -> {
            //FAZER
            AuthenticateUtils authenticateUtils = new AuthenticateUtils(text);
            
            try {
                
                File fileSaved = FileHandler.FileChooserAndSave(text); // ficheiro original
                System.out.println("\nEste é o HMAC:" + authenticateUtils.calculateHMAC(text));
                System.out.println("\n Esta é a private key " + authenticateUtils.getPrivateKey());
                FileHandler.writeFile(authenticateUtils.getPrivateKey(), Paths.get(fileSaved.getParent(), (fileSaved.getName() + "-keyHmac.txt")).toAbsolutePath().toString()); // ficheiro com chave privada
                FileHandler.writeFile(authenticateUtils.calculateHMAC(text), Paths.get(fileSaved.getParent(), (fileSaved.getName() + "-hmac.txt")).toAbsolutePath().toString()); // ficheiro com o hmac
                
            } catch (SignatureException ex) {
                Logger.getLogger(popupUtils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(popupUtils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeyException ex) {
                Logger.getLogger(popupUtils.class.getName()).log(Level.SEVERE, null, ex);
            }


            ////
            
           // authenticateUtils.
                    CloseAndWarn(event);
        });

        Button both = new Button("Both");
        both.setOnAction(event -> {
            //FAZER
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
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