package fr.epita.assistants.myide.domain.entity.any;

import fr.epita.assistants.myide.domain.entity.ExecutionReportEntity;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * This is the Dist class where we handle the Any.features: dist (tarball, zip, etc...).
 *
 * @author Remy.decourcelle@epita.fr
 * @version 1.0
 */
public class Dist implements Feature {

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.getName().contains(".zip")) {
            return;
        }
        System.out.println(fileName);
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            for (File childFile : fileToZip.listFiles()) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    private boolean DistFeature(Project project, Object[] params) {
        Cleanup clean = new Cleanup();
        if (clean.execute(project,params).isSuccess())
        {
            try {
                String srcFile = project.getRootNode().getPath().toString();
                System.out.println(srcFile);
                var name = project.getRootNode().getPath().getFileName().toString() + ".zip";
                var zipPath = project.getRootNode().getPath().getParent().resolve(name);
                FileOutputStream fos = new FileOutputStream(zipPath.toFile());
                File fileToZip = new File(srcFile);
                ZipOutputStream zipOut = new ZipOutputStream(fos);
                zipFile(fileToZip, fileToZip.getName(), zipOut);
                zipOut.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    @Override
    public ExecutionReport execute(Project project, Object... params) {
        return new ExecutionReportEntity(DistFeature(project, params));
    }

    @Override
    public Type type() {
        return Mandatory.Features.Any.DIST;
    }
}
