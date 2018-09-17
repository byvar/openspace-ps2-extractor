package openspace;

import openspace.dsc.FileNode;
import openspace.dsc.FolderNode;
import openspace.dsc.Node;

import java.io.*;

public class Extractor {
    Node rootNode;

    public Extractor(Node rootNode) {
        this.rootNode = rootNode;
    }

    public void extract(File bfFile, File outputDir) {
        try(RandomAccessFile raf = new RandomAccessFile(bfFile, "r")) {
            outputDir.mkdirs();
            extract(raf, rootNode, outputDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extract(RandomAccessFile raf, Node node, File dir) throws IOException {
        if(node instanceof FileNode) {
            FileNode file = (FileNode)node;
            raf.seek(file.getOffset());
            byte[] bytes = new byte[(int) file.getSize()];
            raf.readFully(bytes);
            File fileObj = new File(dir, file.getName());

            try(FileOutputStream fos = new FileOutputStream(fileObj);
                BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                bos.write(bytes);
            }
            System.out.println("Extracting " + fileObj.getPath());
        } else if(node instanceof FolderNode) {
            File dirFile = new File(dir, node.getName() + "/");
            dirFile.mkdirs();
            for(Node child : ((FolderNode) node).getChildren()) {
                extract(raf, child, dirFile);
            }
        }
    }
}
