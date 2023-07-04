package fr.epita.assistants.myide.domain.entity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class NodeEntity implements Node{
    private Path path;
    private Types type;

    private List<Node> children;

    public NodeEntity(Path path) {
        this.path = path;
        this.type = Files.isDirectory(path) ? Types.FOLDER : Types.FILE;
        this.children = new ArrayList<Node>();
        if (isFolder()) {
            try {
                initChildren();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initChildren() throws IOException {
        File dir = new File(path.toString());
        for (File child : dir.listFiles())
        {
            children.add(new NodeEntity(Paths.get(child.getPath())));
        }
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public List<@NotNull Node> getChildren() {
        return children;
    }

    @Override
    public boolean isFile() {
        return Node.super.isFile();
    }

    @Override
    public boolean isFolder() {
        return Node.super.isFolder();
    }
}
