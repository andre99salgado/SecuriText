package sample;

import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Paths;

public class FileHandler {

    public static String mainDirectory = Paths.get(System.getProperty("user.home"), "SecuriTexts").toAbsolutePath().toString();

    public static FileChooser getChooser() {
        FileChooser chooser = new FileChooser();
        File dir = new File(mainDirectory);
        if (!dir.exists()) {
            dir.mkdir();
        }
        chooser.setInitialDirectory(dir);
        return chooser;
    }

    // Abre o File Chooser, grava e devolve o Path onde foi gravado
    public static File FileChooserAndSave(String Text) {
        FileChooser chooser = getChooser();
        File selectedFile = chooser.showSaveDialog(null);
        if (selectedFile != null) {
            writeFile(Text, selectedFile.getAbsoluteFile().toString());
            return selectedFile;
        }
        return null;
    }

    public static File FileChooserAndGetFile() {
        FileChooser chooser = getChooser();
        File selectedFile = chooser.showOpenDialog(null);
        if (selectedFile != null) {
            return selectedFile;
        }
        return null;
    }

    public static String FileChooserAndRead() {
        FileChooser chooser = getChooser();
        File selectedFile = chooser.showOpenDialog(null);
        if (selectedFile != null) {
            return readFile(selectedFile.getAbsolutePath());
        }
        return null;

    }

    public static void writeFile(String Text, String fileName) {
        try {
            FileOutputStream fout = new FileOutputStream(fileName);
            ObjectOutputStream ow = new ObjectOutputStream(fout);
            ow.writeObject(Text);
            ow.flush();
            fout.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static String readFile(String filePath) {

        try {
            FileInputStream f;
            f = new FileInputStream(filePath);

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

}