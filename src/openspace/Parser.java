package openspace;

import openspace.dsc.FileNode;
import openspace.dsc.FolderNode;
import openspace.dsc.Node;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private final File file;
    private final List<String> lines;

    public FolderNode getRootNode() {
        return rootNode;
    }

    private FolderNode rootNode;
    private Pattern folderPattern;
    private Pattern filePattern;

    public Parser(File file) throws IOException {
        this.file = file;
        lines = Files.readAllLines(file.toPath());
    }

    public void parse() throws IOException {
        rootNode = new FolderNode("/");
        Stack<Character> stack = new Stack<>();
        Stack<FolderNode> folderStack = new Stack<>();
        filePattern = Pattern.compile("^([^,]+),([0-9]+),([0-9]+),([0-9]+)$");
        int openIndex = 0;
        FolderNode folderNode = rootNode;
        FileNode fileNode = null;
        for (String line : lines) {
            for (int i = 0; i < line.length(); i++) {
                char current = line.charAt(i);
                // Start file
                if(current == '[') {
                    if(folderNode == null) {
                        folderNode = new FolderNode(line.substring(openIndex, i));
                    }
                    openIndex = i+1;
                    stack.push(current);
                }
                // Start folder
                if(current == '(') {
                    if(folderNode == null) {
                        folderNode = new FolderNode(line.substring(openIndex, i));
                    }
                    folderStack.add(folderNode);
                    folderNode = null;
                    openIndex = i+1;
                    stack.push(current);
                }
                // End file
                if(current == ']') {
                    char last = stack.peek();
                    if(last != '[') throw new IOException("Bad file match at position " + i);
                    stack.pop();

                    String args = line.substring(openIndex, i);
                    Matcher fileMatch = filePattern.matcher(args);
                    if(fileMatch.find()) {
                        // Group 0 is everything
                        FileNode file = new FileNode(fileMatch.group(1),
                                Long.parseLong(fileMatch.group(2)),
                                Long.parseLong(fileMatch.group(3)),
                                Long.parseLong(fileMatch.group(4)));
                        folderNode.addChild(file);
                    }
                }
                // End folder
                if(current == ')') {
                    char last = stack.peek();
                    if(last != '(') throw new IOException("Bad folder match at position " + i);
                    stack.pop();

                    if(folderNode == null) {
                        folderNode = new FolderNode(line.substring(openIndex, i));
                    }
                    folderStack.peek().addChild(folderNode);
                    folderNode = folderStack.pop();

                }

            }
        }
    }




    // The regex way, but could be disadvantageous as there could be multiple groups of matching braces
    /*public void parseRegex() {
        folderPattern = Pattern.compile("^\\(([^\\[\\(]+)(.*)\\)$");
        filePattern = Pattern.compile("^\\[([^,]+),([0-9]+),([0-9]+),([0-9]+)\\]");
        for (String line : lines) {
            Node match = parseRegex(line);
            if(match != null) mainNodes.add(match);
        }
    }

    private Node parseRegex(String str) {
        Matcher folderMatch = folderPattern.matcher(str);
        if(folderMatch.find()) {
            FolderNode folder = new FolderNode(folderMatch.group(0));
            String args = folderMatch.group(1);

            Matcher fileMatch = filePattern.matcher(args);
            while(fileMatch.find()) {
                FileNode file = new FileNode(fileMatch.group(1),
                        Long.parseLong(fileMatch.group(2)),
                        Long.parseLong(fileMatch.group(3)),
                        Long.parseLong(fileMatch.group(4)));
                folder.addChild(file);
                args = fileMatch.replaceFirst("");
                fileMatch = filePattern.matcher(args);
            }

            if(!args.trim().equals("")) {
                Node match = parseRegex(args.trim());
                if(match != null) folder.addChild(match);
            }
            return folder;
        } else return null;
    }*/
}
