/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Hashtable;

/**
 *
 */
public class INodeDirectory extends INode {

    private Hashtable<String, INode> children;

    public INodeDirectory(String name, String path) {
        super(name, path);
        children = new Hashtable<String, INode>();
    }

    public Hashtable<String, INode> getChildren() {
        return children;
    }

}
