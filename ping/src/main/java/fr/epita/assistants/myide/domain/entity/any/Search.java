package fr.epita.assistants.myide.domain.entity.any;

import fr.epita.assistants.myide.domain.entity.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

/**
        * This is the Search class where we handle the Any.features: search.
        *
        * @author Remy.decourcelle@epita.fr
        * @version 1.0
        */
public class Search implements Feature {

    private boolean runThrowChild(Node father, String toSearch) { //AMELIORABLE
        for (Node child : father.getChildren())
        {
            System.out.println(child.getPath());
            if (child.isFile() && searchFeature(child.getPath(), toSearch))
                return true;
            boolean response = runThrowChild(child, toSearch);
            if (response)
                return true;
        }
        return false;
    }


    private boolean searchFeature(Path pathFile, String toSearch)
    {
        /*if (pathFile.getFileName().toString().equals("AnyTests.class") || pathFile.getFileName().getFileName().toString().equals("AnyTests.java"))
            return false;*/
        try {
            BufferedReader rd = new BufferedReader(new FileReader(pathFile.toFile()));
            String data = null;
            while((data = rd.readLine()) != null)
            {
                data = data.toLowerCase();
                toSearch = toSearch.toLowerCase();
                if (data.contains(toSearch))
                {
                    rd.close();
                    return true;
                }
            }
            rd.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }


    private boolean searchFeatures(Project project, Object... params)
    {
        if (params == null || params.length == 0)
            return true;
        String toSearch = params[0].toString();
        return runThrowChild(project.getRootNode(), toSearch);
    }

    @Override
    public ExecutionReport execute(Project project, Object... params) {
        return new ExecutionReportEntity(searchFeatures(project, params));
    }

    @Override
    public Type type()
    {
        return Mandatory.Features.Any.SEARCH;
    }
}
