package openspace.dsc;

import java.util.ArrayList;
import java.util.List;

public class FolderNode extends Node {

    public List<Node> getChildren() {
        return children;
    }

    private List<Node> children;

    public FolderNode(String name) {
        super(name);
        children = new ArrayList<>();
    }

    public void addChild(Node node) {
        children.add(node);
    }
}
