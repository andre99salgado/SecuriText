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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sample.FileHandler.FileChooserAndSave;
import static sample.FileHandler.getChooser;

public class Controller implements Initializable {


    @FXML
    private TextField txtArea;

    @FXML
    private TextArea txtAreaTotal;

    @FXML
    private VBox anchorid;

    private boolean foiAberto = false;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void openFile(ActionEvent event) {

        foiAberto = true;
        FileChooser chooser = getChooser();
        File selectedFile = chooser.showOpenDialog(null);
        if (selectedFile != null) {
            txtArea.setText(selectedFile.getName());
            txtAreaTotal.setText(readFile(selectedFile.getName()));
            txtAreaTotal.requestFocus();
        }
    }

    //TODO: Era melhor que só aparecesse o explorador caso o ficheiro nao existisse antes, ou quando se clica em "Save As..."
    @FXML
    void createFile(ActionEvent event) {
        //Se não foi aberto usando o Open
        String text = txtAreaTotal.getText();
        if (!foiAberto) {
            openCipherSelect((Stage) txtAreaTotal.getScene().getWindow(), text);

        } else {
            FileChooserAndSave(text);
        }

    }


    private String readFile(String fileName) {

        try {
            FileInputStream f;
            f = new FileInputStream(new File(Paths.get(System.getProperty("user.home"), "SecuriTexts", fileName).toString()));

            ObjectInputStream fileStream;
            fileStream = new ObjectInputStream(f);
            return (String) fileStream.readObject();
        } catch (EOFException e) {
            System.out.println("\n\nEmpty File \n\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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

    public void openCipherSelect(Stage stage, String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("select_cipher.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            SelectCipherController controller = fxmlLoader.<SelectCipherController>getController();
            controller.setText(text);
            stage.setScene(scene);
            stage.setTitle("Select Option.");
            stage.show();

        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

}
