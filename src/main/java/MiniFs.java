/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.princeton.cs.introcs.StdOut;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 */
public class MiniFs implements FileSystem {

    private final INodeDirectory root;

    private static INodeDirectory lastDir;

    private static String targetName, head, tail;           // Used in digger, mkdir and touch

    Hashtable<String, String> symTable = new Hashtable<String, String>();   // Hashtable to store symbolic links

    public MiniFs() {
        root = new INodeDirectory("/", "/");
    }

    @Override
    public void mkdir(String path) {
        INodeDirectory directory;
        separator(path);                                    // Separates the last name in path from the rest
        if (symTable.get(head) != null) {                   // If head is a key in the symbolic link hashtable...
            directory = digger(symTable.get(head) + tail, false);   //...the dir is created in the dir head is pointing to
            path = symTable.get((head));                    // Update path for correct printing
        } else {
            directory = digger(path, false);                // If no such link, path is the supplied path
        }

        if (directory != null) {                            // Only create new dir if returned dir isn't null
            directory.getChildren().put(targetName, new INodeDirectory(targetName, path));  // Put new dir in hashtable
            StdOut.println("Directory " + targetName + " successfully created at " + path + ".");
        }
    }

    @Override
    public void touch(String path) {
        INodeDirectory directory;
        separator(path);                                    // Separates the last name in a path from the rest
        if (symTable.get(head) != null) {                   // If head is a key in the symbolic link hashtable...
            directory = digger(symTable.get(head) + tail, false);   //...the file is created in the dir head is pointing to
            path = symTable.get(head);                      // Update path for correct printing
        } else {
            directory = digger(path, false);                // If no such link, path is the supplied path
        }

        if (directory != null) {                            // Only create new file if returned dir isn't null
            directory.getChildren().put(targetName, new INodeFile(targetName, path));       // Put new file in hashtable
            StdOut.println("File " + targetName + " successfully created at " + path + ".");
        }

    }

    public void separator(String path) {
        if (path.substring(1).contains("/")) {              // If the path contains more than one "/"...
            head = path.substring(0, path.lastIndexOf('/'));    // ...substring accordingly
            tail = path.substring(path.lastIndexOf('/'));
        } else {
            head = path;                                    // If path only contains one "/" there is no tail
            tail = null;
        }
    }

    @Override
    public void ln(String source, String target) {
        if (symTable.containsKey(source)) {                 // If the hashtable contains source, source is already linked
            StdOut.println("Error: " + source + " is already linked to " + symTable.get(source) + "");
        } else {
            if (digger(source, true) != null && digger(target, true) != null) { // If the paths are valid...
                symTable.put(source, target);                                   // Link them by adding to hashtable
                StdOut.println(source + " linked with " + target + ".");
            }
        }

    }

    @Override
    public void find(String name) {
        lastDir = root;
        find(lastDir, name);
    }

    private void find(INodeDirectory curDir, String name) {
        for (String key : curDir.getChildren().keySet()) {  // Iterate over current dirs hashtable
            if (key.equals(name)) {                         // If we find an entry with the target name...
                StdOut.println("Find: Found entry at " + curDir.getChildren().get(key).getPath() + "."); // ...print out where it was found
            }
            if (curDir.getChildren().get(key) instanceof INodeDirectory) {      // If the current key corresponds to a dir...
                find((INodeDirectory) curDir.getChildren().get(key), name);  // ...start searching for that dir as well
            }
        }
    }

    @Override
    public void findc(String criteria) {
        lastDir = root;
        findc(lastDir, criteria);
    }

    private void findc(INodeDirectory curDir, String criteria) {
        for (String key : curDir.getChildren().keySet()) {  // Iterate over the hashtable for the current dir

            if (criteria.charAt(0) == '*') {                // First char is *
                if (key.endsWith(criteria.substring(1))) {  // If an INode name ends with the criteria string (* omitted) -> hit
                    StdOut.println("Findc: Found entry at " + curDir.getChildren().get(key).getPath() + ".");
                }
            } else if (criteria.endsWith("*")) {            // Last char is *
                // If an INode name starts with the critera string (* omitted) -> hit
                if (key.substring(0, criteria.indexOf('*')).equals(criteria.substring(0, criteria.indexOf('*')))) {
                    StdOut.println("Findc: Found entry at " + curDir.getChildren().get(key).getPath() + ".");
                }
            } else {                                        // * is in the middle
                // If an INode name starts with the string before * and ends with string after * -> hit
                if (key.substring(0, criteria.indexOf('*')).equals(criteria.substring(0, criteria.indexOf('*'))) && key.endsWith(criteria.substring(criteria.indexOf('*') + 1))) {
                    StdOut.println("Findc: Found entry at " + curDir.getChildren().get(key).getPath() + ".");
                }
            }

            if (curDir.getChildren().get(key) instanceof INodeDirectory) {  // If the key corresponds to a dir...
                findc((INodeDirectory) curDir.getChildren().get(key), criteria);    // ...search for that dir as well
            }
        }
    }

    @Override
    public void cycles() {
        LinkedList<String> visited = new LinkedList<String>();  // Saves which keys that are visited
        String value;                                       // For readability
        Set<String> strings = symTable.keySet();            // Set of all keys in symTable
        String[] keys = strings.toArray(new String[strings.size()]);    // Cast to array for sorting
        Arrays.sort(keys);

        for (String key : keys) {                           // Iterate over keys in array
            value = symTable.get(key);
            if (key.contains(value)) {                      // If value is a substring of key, cycle is found
                StdOut.println(key + " -> " + value + " causes a cycle.");
                break;
            }
            visited.add(key);                               // Mark key as visited
            if (symTable.containsKey(value)) {              // If value exists as key in symTable...
                if (visited.contains(value)) {              // ...and value as key is visited, cycle is found
                    StdOut.println(key + " -> " + value + " causes a cycle.");
                    break;
                }
            }
        }
    }

    private INodeDirectory digger(String path, boolean lastCheck) {
        if (path.charAt(0) != '/') {                        // All valid paths start with a slash
            StdOut.println("Error: Invalid path.");
            return null;
        }

        String[] names = path.split("/");                   // Return an array of all directory names
        lastDir = root;                                     // Always start at root
        targetName = names[names.length - 1];               // Last entry of names will be the file/dir to be created
        int loopLength;
        if (lastCheck) {
            loopLength = names.length;
        } else {
            loopLength = names.length - 1;
        }


        for (int i = 1; i < loopLength; i++) {         // Loop through for each but the first and last names
            if (lastDir.getChildren().get(names[i]) == null) {  // If the name doesn't exist in the dirs hashtable...
                StdOut.println("Error: Invalid path.");     // ...the path is invalid
                return null;
            }
            if (lastDir.getChildren().get(names[i]) instanceof INodeDirectory) {    // If it does exist and is a dir...
                lastDir = (INodeDirectory) lastDir.getChildren().get(names[i]);     // ...jump to that dir and continue
            }
        }
    return lastDir;
    }

}
