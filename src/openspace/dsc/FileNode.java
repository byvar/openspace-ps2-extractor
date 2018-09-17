package openspace.dsc;

public class FileNode extends Node {

    private long offset;
    private long size;
    private long unk;

    public FileNode(String name, long offset, long size, long unk) {
        super(name);
        this.offset = offset;
        this.size = size;
        this.unk = unk;
    }

    public long getOffset() {
        return offset;
    }
    public long getSize() {
        return size;
    }
}
