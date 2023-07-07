package fr.epita.assistants.utils.front;

import fr.epita.assistants.Interface;
import javafx.scene.control.TextInputDialog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static fr.epita.assistants.utils.front.CryptoManager.generateRandomKey;

/**
 * This is the PasswordManager class which we use to simplify the process of the i3lock-like feature
 *
 * @author remy.decourcelle@epita.fr
 * @version 1.0
 */
public class PasswordManager {
    private static String projectmdp = null;

    private static String projectKey = null;
    private static Path pointVault = null;

    public static void startVaultProject()
    {
        projectmdp = null;
        pointVault = null;
        getPointVault();
        if (pointVault == null)
            createPointVault();
        searchMdp();
        searchKey();
    }

    private static void createPointVault()
    {
        String folderPath = Interface.project.getRootNode().getPath().toString() + "\\.vault";

        Path folder = Paths.get(folderPath);

        try {
            Files.createDirectories(folder);
            pointVault = folder;
            System.out.println("Dossier créé avec succès !");
        } catch (Exception e) {
            System.out.println("Erreur lors de la création du dossier : " + e.getMessage());
        }

    }

    private static void getPointVault()
    {
        String folderPath = Interface.project.getRootNode().getPath().toString()+"\\.vault"; // Remplacez par le chemin souhaité
        System.out.println(folderPath);

        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            pointVault = Paths.get(folderPath);
            //System.out.println("Folder exist");
        } else {
            System.err.println("the Folder does not exist !");
        }
    }

    private static void searchMdp()
    {
        String filePath = pointVault.toString() + "\\.mdp";

        File file = new File(filePath);

        if (!file.exists()) {
            return;
        }
        else if (!file.isFile()){
            file.delete();
            System.err.println("file deleted");
        }
        projectmdp = Arrays.stream(FileManager.readFileContent(filePath).split("\n")).findFirst().get();
        if(projectmdp.isEmpty() || projectmdp.isBlank())
            projectmdp = FileManager.readFileContent(filePath);
        //System.err.println(projectmdp);
    }

    private static void searchKey()
    {
        String filePath = pointVault.toString() + "\\.key";

        File file = new File(filePath);

        if (!file.exists()) {
            createKey();
            return;
        }
        else if (!file.isFile()){
            file.delete();
            createKey();
            return;
        }
        projectKey = Arrays.stream(FileManager.readFileContent(filePath).split("\n")).findFirst().get();
        if(projectKey.isEmpty() || projectKey.isBlank())
            projectKey = FileManager.readFileContent(filePath);
    }

    private static void updateMdp(String mdp)
    {
        String filePath = pointVault.toString() + "\\.mdp";
        File file = new File(filePath);

        try {
            Files.createFile(file.toPath());
            Interface.updateTree();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(mdp);
            projectmdp = mdp;
            System.out.println("Contenu ajouté avec succès !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateKey(String key)
    {
        String filePath = pointVault.toString() + "\\.key";
        File file = new File(filePath);

        try {
            Files.createFile(file.toPath());
            Interface.updateTree();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(key);
            projectKey = key;
            System.out.println("Contenu ajouté avec succès !");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //updateFileRight(filePath);
    }

    private static void updateFileRight(String path)//Does not work
    {
        Path file = Paths.get(path);

        // Crée un ensemble de permissions à modifier
        Set<PosixFilePermission> permissions = new HashSet<>();
        permissions.add(PosixFilePermission.OWNER_READ);
        permissions.add(PosixFilePermission.GROUP_READ);
        permissions.add(PosixFilePermission.OTHERS_READ);

        try {
            Files.setPosixFilePermissions(file, permissions);
            System.out.println("Les permissions du fichier ont été modifiées avec succès !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkPassword(String pass)
    {
        return projectmdp.equals(pass);
    }

    public static boolean mdpIsPresent()
    {
        return projectmdp != null;
    }

    public static boolean keyIsPresent(){return projectKey != null;}

    public static void createMdp()
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Mot de passe");
        dialog.setHeaderText(null);
        dialog.setContentText("Veuillez entrer le mot de passe :");
        String mdp = dialog.showAndWait().get();
        dialog = new TextInputDialog();
        dialog.setTitle("Mot de passe");
        dialog.setHeaderText(null);
        dialog.setContentText("Veuillez retaper le mot de passe :");
        String mdp2 = dialog.showAndWait().get();
        if(mdp.equals(mdp2))
            updateMdp(mdp);
        else
            createMdp();
    }

    public static void createKey() {
        String secretKey = null;
        try {
            secretKey = generateRandomKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        updateKey(secretKey);
    }

    public static String getKey(){return projectKey;}
}
