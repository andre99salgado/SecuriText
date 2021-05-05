package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

public class SelectCipherController implements Initializable {

    private String text;

    @FXML
    private Button authenticateButton;

    @FXML
    private Button encryptButton;

    @FXML
    private Button bothButton;

    @FXML
    private HBox hbox_area;

    //TODO: ver outra forma de passar texto
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Esperar que o outro controlador mude o texto
        Platform.runLater(() -> {
            System.out.println(text);
        });
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @FXML
    public void authenticateClick(ActionEvent event) {
        popup();
    }

    @FXML
    public void encryptClick(ActionEvent event) {
    }

    @FXML
    public void bothClick(ActionEvent event) {
    }


    public void popup() {
        Stage currentStage = (Stage) hbox_area.getScene().getWindow();

        currentStage.setTitle("Popup Example");
        final Popup popup = new Popup();
        popup.setX(currentStage.getX());
        popup.setY(currentStage.getY());
        popup.getContent().addAll(new HBox());

        Button show = new Button("Show");
        show.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                popup.show(currentStage);
            }
        });

        Button hide = new Button("Hide");
        hide.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                popup.hide();
            }
        });
        Button test = new Button("Test");

        HBox layout = new HBox(10);
        layout.setStyle("-fx-background-color: cornsilk; -fx-padding: 10;");
        layout.getChildren().addAll(show, hide, test);
        currentStage.setScene(new Scene(layout));
        currentStage.show();
    }
}
