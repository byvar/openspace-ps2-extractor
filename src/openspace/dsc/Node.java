package openspace.dsc;

public abstract class Node {
    protected String name;
    public String getName() {
        return name;
    }

    public Node(String name) {
        this.name = name;
    }
}
