package fr.epita.assistants.myide.domain.service;

import ch.qos.logback.core.util.FileUtil;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.NodeEntity;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.entity.ProjectEntity;
import fr.epita.assistants.utils.Exceptions;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class NodeServiceEntity implements NodeService{

    private ProjectEntity project;

    public NodeServiceEntity(ProjectEntity project)
    {
        this.project = project;
    }

    @Override
    public Node update(Node node, int from, int to, byte[] insertedContent) {
        String lines;
        try {
            lines = new String(Files.readAllBytes(node.getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileWriter writer;
        try {
            writer = new FileWriter(node.getPath().toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String toInsert = new String(insertedContent, StandardCharsets.UTF_8);
        System.out.println("lines =" + lines);

        String updated = lines.substring(0, from) + toInsert + lines.substring(to);
        System.out.println(updated);
        try {
            writer.write(updated);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return node;
    }

    public Node searchFather(Node node, Node father) { //AMELIORABLE
        for (Node child : father.getChildren()) {
            if (child == node)
                return father;
            Node response = searchFather(node, child);
            if (response != null)
                return response;
        }
        return null;
    }

    @Override
    public boolean delete(Node node) {
        Node father = searchFather(node, project.getRootNode());
        if (father != null){
            if (father.getChildren().remove(node)) {
                try {
                    if (node.isFile())
                        Files.delete(node.getPath());
                    else if (node.isFolder()) {
                        FileUtils.deleteDirectory(node.getPath().toFile());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public Node create(Node folder, String name, Node.Type type) {
        if (folder.getType() == Node.Types.FILE)
            throw new InvalidParameterException("File can't have children");
        Path newpath = Paths.get(folder.getPath().toString() + "/" + name);
        if (type == Node.Types.FILE)
        {
            try {
                Files.createFile(newpath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if (type == Node.Types.FOLDER)
        {

            try {
                Files.createDirectories(newpath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Node newnode = new NodeEntity(newpath);
        folder.getChildren().add(newnode);
        return newnode;
    }

    @Override
    public Node move(Node nodeToMove, Node destinationFolder) {
        if (destinationFolder.getType() == Node.Types.FILE)
            throw new InvalidParameterException("File can't have children");
        String name = nodeToMove.getPath().getFileName().toString();
        Path newpath = Paths.get(destinationFolder.getPath().toString() + "/" + name);

        try
        {
            Files.move(nodeToMove.getPath(), newpath, REPLACE_EXISTING);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        Node father = searchFather(nodeToMove, project.getRootNode());
        father.getChildren().remove(nodeToMove);

        Node newnode = new NodeEntity(newpath);
        destinationFolder.getChildren().add(newnode);
        return newnode;
    }
}
