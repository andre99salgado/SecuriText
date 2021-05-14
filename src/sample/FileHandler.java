package sample;

import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;

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

    //escreve texto completo num file
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

    //No final podemos fazer override para melhorar o código
    //Serve para escrever nos ficheiros o conjunto de parâmetros no keys-and-iv.txt para poder abrir os ficheiros
    //Lista com chave de encrypt, chave privada rsa para hmac e o próprio hmac
    public static void writeFileArrayString(ArrayList<String> Text, String fileName) {

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

    //serve para ler um texto completo saído de um file
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


    //Serve para ler fos ficheiros o conjunto de parâmetros no keys-and-iv.txt (exemplo onde usamos) para poder abrir os ficheiros
    //Lista com chave de encrypt, chave privada rsa para hmac e o próprio hmac
    public static ArrayList<String> readFileStringList(String filePath) {

        try {
            FileInputStream f;
            f = new FileInputStream(filePath);
            ObjectInputStream fileStream;
            fileStream = new ObjectInputStream(f);
            return (ArrayList<String>) fileStream.readObject();
        } catch (EOFException e) {
            System.out.println("\n\nEmpty File \n\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}