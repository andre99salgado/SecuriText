package sample;

import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;

public class FileHandler {

    public static FileChooser getChooser() {
        FileChooser chooser = new FileChooser();
        File dir = new File(Paths.get(System.getProperty("user.home"), "SecuriTexts").toAbsolutePath().toString());
        if (!dir.exists()) {
            dir.mkdir();
        }
        chooser.setInitialDirectory(dir);
        return chooser;
    }

    public static void FileChooserAndSave(String Text) {
        FileChooser chooser = getChooser();
        File selectedFile = chooser.showSaveDialog(null);
        if (selectedFile != null) {
            writeFile(Text, selectedFile.getAbsoluteFile().toString());
        }
    }

    public static void writeFile(String Text, String fileName) {
        try {
            FileOutputStream fout = new FileOutputStream(fileName);
            ObjectOutputStream ow = new ObjectOutputStream(fout);
            ow.writeObject(Text);
            fout.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
