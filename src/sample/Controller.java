package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.*;
import java.net.URL;
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

    FileInputStream f;
    ObjectInputStream fileStream;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void openFile(ActionEvent event) {

        FileChooser chooser = new FileChooser();
        File dir = new File("C:\\SecuriTexts");
        chooser.setInitialDirectory(dir);
        File selectedFile = chooser.showOpenDialog(null);
        if (selectedFile != null) {
            System.out.println("File selected: " + selectedFile.getName());
            txtArea.setText(selectedFile.getName());
            txtAreaTotal.setText(readFile(selectedFile.getName()));
            txtAreaTotal.requestFocus();
        }
    }

    @FXML
    void createFile(ActionEvent event) {
        String textFileName = txtArea.getText();
        writeFile(txtAreaTotal.getText(), textFileName);
    }

    void writeFile(String Text, String fileName) {
        try {
            FileOutputStream fout = new FileOutputStream("C:\\SecuriTexts\\" + fileName);
            ObjectOutputStream ow = new ObjectOutputStream(fout);
            ow.writeObject(Text);
            fout.close();
            System.out.println("success...");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    String readFile(String fileName) {
        try {
            f = new FileInputStream(new File("C:\\SecuriTexts\\" + fileName));
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
    private void closeButtonAction(ActionEvent event){
        Platform.exit();
        System.exit(0);
    }


}
