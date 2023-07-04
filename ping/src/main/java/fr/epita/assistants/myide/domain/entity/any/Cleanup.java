package fr.epita.assistants.myide.domain.entity.any;

import fr.epita.assistants.myide.domain.entity.*;
import fr.epita.assistants.myide.domain.service.NodeServiceEntity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;

public class Cleanup implements Feature {

    private Node searchNode(Node node, Node father) { //AMELIORABLE
        for (Node child : father.getChildren()) {
            if (child == node)
                return child;
            Node response = searchNode(node, child);
            if (response != null)
                return response;
        }
        return null;
    }
    private Node searchNodeFromPath(Path path, Node father) { //AMELIORABLE
        for (Node child : father.getChildren()) {
            if (child.getPath().getFileName().equals(path))
                return child;
            Node response = searchNodeFromPath(path, child);
            if (response != null)
                return response;
        }
        return null;
    }

    private boolean cleanupFeatures(Project project)// may does not work with regex lines in ignore
    {
        System.err.println("CLEANUP START ");

        Node rootNode = project.getRootNode();
        Node ideFile = null;
        for (Node child : rootNode.getChildren()){
            if (child.getPath().getFileName().toString().equals(".myideignore"))
            {
                System.out.println("in if");
                ideFile = child;
                break;
            }
        }
        if (ideFile == null)
            throw new InvalidParameterException("No file named .myideignore");

        System.err.println("CLEANUP START DELETING ");

        String path = ideFile.getPath().toString();
        NodeServiceEntity nser = new NodeServiceEntity((ProjectEntity) project);// May bug cause of the cast
        try (BufferedReader lecteur = new BufferedReader(new FileReader(path))) {
            String ligne;
            while ((ligne = lecteur.readLine()) != null) {
                System.out.println(ligne); // Traitez la ligne selon vos besoins
                nser.delete(searchNodeFromPath(Paths.get(ligne), project.getRootNode()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.err.println("CLEANUP END ");
        return true;
    }


    @Override
    public ExecutionReport execute(Project project, Object... params) {
        return new ExecutionReportEntity(cleanupFeatures(project));
    }

    @Override
    public Type type() {
        return Mandatory.Features.Any.CLEANUP;
    }
}
