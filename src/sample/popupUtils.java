package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class popupUtils {


    public static void popup(Stage currentStage, Node... es) {

        currentStage.setTitle("Popup Example");
        final Popup popup = new Popup();
        popup.setX(currentStage.getX());
        popup.setY(currentStage.getY());
        popup.getContent().addAll(new HBox());


        HBox layout = new HBox(10);
        layout.setStyle("-fx-background-color: cornsilk; -fx-padding: 10;");
        layout.getChildren().addAll(es);
        Stage stage = new Stage();
        stage.setScene(new Scene(layout));
        stage.show();
    }

    public static void selectionPopup(Stage currentStage, String text) {
        Button show = new Button("Encrypt");
        show.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CipherUtil cipherUtil = new CipherUtil(text);
                File fileSaved = FileHandler.FileChooserAndSave(cipherUtil.getEncryptedString());
                FileHandler.writeFile(cipherUtil.getKeyAsString(), Paths.get(fileSaved.getParent(), (fileSaved.getName() + ".key.txt")).toAbsolutePath().toString());
                //TODO: Deve mostrar outro POPUP a dizer para remover o ficheiro daquele sítio
            }
        });

        Button hide = new Button("Hide");
        hide.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ((Popup) event.getSource()).show(currentStage);
            }
        });
        Button test = new Button("Test");
        popup(currentStage, show, hide, test);

    }

}
