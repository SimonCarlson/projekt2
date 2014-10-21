/**
 * Created by Simon Carlson on 2014-10-21.
 */
public class SymLink extends INode {

    private String name;

    private String path;

    public SymLink(String name, String path) {
        super(name, path);
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }
}
