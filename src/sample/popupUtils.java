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
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class popupUtils {

    private static KeysUtils keyaux;

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
            keyaux = new KeysUtils(cipherUtil.getKeyAsString(), "", "", "");
            FileHandler.writeFileArrayString(keyaux.getKeysF(), Paths.get(fileSaved.getParent(), (getFileType(fileSaved.getName()) + "_keys-and-iv.txt")).toAbsolutePath().toString());
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

                keyaux = new KeysUtils("", authenticateUtils.getPrivateKey(), authenticateUtils.calculateHMAC(text), "");


                FileHandler.writeFileArrayString(keyaux.getKeysF(), Paths.get(fileSaved.getParent(),
                        (getFileType(fileSaved.getName()) + "_keys-and-iv.txt")).toAbsolutePath().toString()); // ficheiro com chave privada
                //----------------------------

            } catch (SignatureException | InvalidKeyException | NoSuchAlgorithmException ex) {
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

                keyaux = new KeysUtils(cipherUtil.getKeyAsString(), authenticateUtils.getPrivateKey(), authenticateUtils.calculateHMAC(encriptada), "");
                assert fileSaved != null;
                FileHandler.writeFileArrayString(keyaux.getKeysF(), Paths.get(fileSaved.getParent(), (getFileType(fileSaved.getName()) + "_keys-and-iv.txt")).toAbsolutePath().toString());

            } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException ex) {
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

    public static void MessagePopup(Stage currentStage, String message) {
        Label label = new Label(message);
        Button okButton = new Button("OK");
        okButton.setOnAction(popupUtils::closeFromEvent);
        popup(currentStage, label, okButton);

    }

    private static String getFileType(String nome) {
        System.out.println(nome);
        if (nome != null) {
            System.out.println("asfafs: " + getExtensionByStringHandling(nome).get());
            String[] partes = nome.split(getExtensionByStringHandling(nome).get());
            System.out.println(partes[0]);
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