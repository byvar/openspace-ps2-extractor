package openspace;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Command(mixinStandardHelpOptions = true, version = "OpenSpace PS2 Extractor v0.1")
public class Main implements Runnable {
    @Option(names = { "-v", "--verbose" }, description = "Be verbose.")
    private boolean verbose = false;

    @Parameters(index="0", paramLabel = "BFfile", description = "The BF file to unpack.")
    private File bfFile;
    private File dscFile;

    @Parameters(index="1", paramLabel = "OutDir", description = "Directory where the files will be extracted.")
    private File outputDirectory;

    private Parser parser;
    private Extractor extractor;

    public static void main(String[] args) {
        CommandLine.run(new Main(), System.out, args);
    }

    @Override
    public void run() {
        if(bfFile != null) {
            try {
                String path = bfFile.getAbsolutePath();
                if(!path.toLowerCase().endsWith(".bf") || !bfFile.isFile() || !bfFile.exists()) throw new FileNotFoundException("BF file not found");
                dscFile = new File(path.substring(0,path.length()-2) + "DSC");
                if(!dscFile.isFile() || !dscFile.exists()) throw new FileNotFoundException("DSC file not found");
                parser = new Parser(dscFile);
                parser.parse();
                extractor = new Extractor(parser.getRootNode());
                extractor.extract(bfFile, outputDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
