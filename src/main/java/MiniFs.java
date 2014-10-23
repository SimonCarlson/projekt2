/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.princeton.cs.introcs.StdOut;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 */
public class MiniFs implements FileSystem {

    private final INodeDirectory root;

    private static INodeDirectory lastDir;

    private static String targetName;          // Used in digger

    Hashtable<String, String> symTable = new Hashtable<String, String>();   // Hashtable to store symbolic links

    public MiniFs() {
        root = new INodeDirectory("/");
    }

    @Override
    public void mkdir(String path) {
        INodeDirectory directory = digger(path, false);

        if (directory != null) {                            // Only create new dir if returned dir isn't null
            directory.getChildren().put(targetName, new INodeDirectory(targetName));  // Put new dir in hashtable
            StdOut.println("Directory " + targetName + " successfully created at " + path + ".");
        }
    }

    @Override
    public void touch(String path) {
        INodeDirectory directory = digger(path, false);

        if (directory != null) {                            // Only create new file if returned dir isn't null
            directory.getChildren().put(targetName, new INodeFile(targetName));       // Put new file in hashtable
            StdOut.println("File " + targetName + " successfully created at " + path + ".");
        }

    }

    @Override
    public void ln(String source, String target) {
        if (symTable.containsKey(source)) {                 // If the hashtable contains source, source is already linked
            StdOut.println("Error: " + source + " is already linked to " + symTable.get(source) + "");
        } else {
            if (digger(source, true) != null && digger(target, true) != null) { // If the paths are valid...
                String newChild = target.substring(target.lastIndexOf('/') + 1);
                digger(source, true).getChildren().put(newChild, digger(target, true));
                symTable.put(source, target);
                StdOut.println(source + " linked with " + target + ".");
            }
        }
    }

    @Override
    public void find(String name) {
        lastDir = root;
        StringBuilder path = new StringBuilder();
        find(lastDir, name, path);
    }

    private void find(INodeDirectory curDir, String name, StringBuilder path) {

        if (curDir.getName().equals(name)) {                // If we are in the dir we are looking for, print out
            StdOut.println("Find: Found entry at " + path + curDir.getName() + ".");
        }

        path.append(curDir.getName());                      // Build up path
        path.append("/");


        for (String key : curDir.getChildren().keySet()) {  // Iterate over current dirs hashtable
            if (curDir.getChildren().get(key) instanceof INodeDirectory) {      // If the current key corresponds to a dir...
                find((INodeDirectory) curDir.getChildren().get(key), name, path);  // ...start searching for that dir as well
            }
            if (key.equals(name) && curDir.getChildren().get(key) instanceof INodeFile) {   // If we find an entry with the target name...
                StdOut.println("Find: Found entry at " + path + key + "."); // ...print out where it was found
            }
        }

        path.delete(path.length() - (curDir.getName().length() + 1), path.length());    // Remove the dir we visited
    }

    @Override
    public void findc(String criteria) {
        lastDir = root;
        StringBuilder path = new StringBuilder();

        if (!criteria.contains("*")) {                      // Command must contain a *
            StdOut.println("Error: invalid command");
            return;
        }

        if (criteria.charAt(0) == '*') {                    // If criteria starts with *...
            criteria = criteria.substring(1);               // ...remove it from criteria
            findc(lastDir, criteria, path, 0);              // Call helper method with case 0
        } else if (criteria.endsWith("*")) {                // If criteria ends with *...
            criteria = criteria.substring(0, criteria.length() - 1);    // ...remove it from criteria
            findc(lastDir, criteria, path, 1);              // Call helper method with case 1
        } else {
            findc(lastDir, criteria, path, 2);              // Call helper method with case 2
        }
    }

    private void findc(INodeDirectory curDir, String criteria, StringBuilder path, int pos) {
        if (pos == 0) {
            if (curDir.getName().endsWith(criteria)) {      // If current directory ends with criteria, hit
                StdOut.println("Findc: Found entry at " + path + curDir.getName()); // Print out current path
            }
        } else if (pos == 1) {
            if (curDir.getName().startsWith(criteria)) {    // If current directory starts with criteria, hit
                StdOut.println("Findc: Found entry at " + path + curDir.getName()); //Print out current path
            }
        } else {
            String head = criteria.substring(0, criteria.indexOf('*'));     // String before *
            String tail = criteria.substring(criteria.indexOf('*') + 1);    // String after *
            if (curDir.getName().startsWith(head) && curDir.getName().endsWith(tail)) { // If current directory starts and ends with correct strings, hit
                StdOut.println("Findc: Found entry at " + path + curDir.getName()); // Print out current path
            }
        }

        path.append(curDir.getName());                      // Build up path
        path.append("/");

        for (String key : curDir.getChildren().keySet()) {  // Iterate over keys in hashtable for current directory
            if (curDir.getChildren().get(key) instanceof INodeDirectory) {  // If we find a directory, start searching for that directory
                findc((INodeDirectory) curDir.getChildren().get(key), criteria, path, pos);
            }

            if (pos == 0) {                                 // Entire codeblock follows same logic as above
                if (key.endsWith(criteria) && curDir.getChildren().get(key) instanceof INodeFile) {
                    StdOut.println("Findc: Found entry at " + path + key);
                }
            } else if (pos == 1) {
                if (key.startsWith(criteria) && curDir.getChildren().get(key) instanceof INodeFile) {
                    StdOut.println("Findc: Found entry at " + path + key);
                }
            } else {
                String head = criteria.substring(0, criteria.indexOf('*'));
                String tail = criteria.substring(criteria.indexOf('*') + 1);
                if (key.startsWith(head) && key.endsWith(tail) && curDir.getChildren().get(key) instanceof INodeFile) {
                    StdOut.println("Findc: Found entry at " + path + key);
                }
            }
        }

        path.delete(path.length() - (curDir.getName().length() + 1), path.length());    // Remove the path we came from
    }

    @Override
    public void cycles() {
        LinkedList<String> visited = new LinkedList<String>();  // Saves which keys that are visited
        String value;                                       // For readability
        Set<String> keys = symTable.keySet();

        mainloop: for (String key : keys) {                 // Iterate over keys in array
            value = symTable.get(key);
            visited.add(key);                               // Mark key as visited
            for (String entry : visited) {
                if (entry.contains(value)) {
                    StdOut.println(key + " -> " + value + " causes a loop");
                    break mainloop;
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


        for (int i = 1; i < loopLength; i++) {
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
