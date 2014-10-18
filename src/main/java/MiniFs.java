/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import edu.princeton.cs.introcs.StdOut;

/**
 *
 */
public class MiniFs implements FileSystem {

    private final INodeDirectory root;

    private static INodeDirectory lastDir;

    private static String targetName;

    public MiniFs() {
      root = new INodeDirectory("/", "/");
    }

    @Override
    public void mkdir(String path) {
        INodeDirectory directory = digger(path);            // Return the dir to create the new dir in
        if (directory != null) {                            // Only create new dir if returned dir isn't null
            directory.getChildren().put(targetName, new INodeDirectory(targetName, path));  // Put new dir in hashtable
            StdOut.println("Directory \"" + targetName + "\" successfully created at \"" + path + "\".");
        }
    }

    @Override
    public void touch(String path) {
        INodeDirectory directory = digger(path);            // Return the dir to create new file in
        if (directory != null) {                            // Only create new file if returned dir isn't null
            directory.getChildren().put(targetName, new INodeFile(targetName, path));             // Put new file in hashtable
            StdOut.println("File \"" + targetName + "\" successfully created at \"" + path + "\".");
        }

    }

    @Override
    public void ln(String source, String target) {

    }

    @Override
    public void find(String name) {
        lastDir = root;
        find(lastDir, name);
    }

    private void find(INodeDirectory curDir, String name) {
        for (String key : curDir.getChildren().keySet()) {  // Iterate over current dirs hashtable
            if (key.equals(name)) {                         // If we find an entry with the target name...
                StdOut.println("Find: Found entry at \"" + curDir.getChildren().get(key).getPath() + "\"."); // ...print out where it was found
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
        for (String key : curDir.getChildren().keySet()) {

            if (criteria.charAt(0) == '*') {
                if (key.endsWith(criteria.substring(1))) {
                    StdOut.println("Findc: Found entry at \"" + curDir.getChildren().get(key).getPath() + "\".");
                }
            } else if (criteria.endsWith("*")) {
                if (key.substring(0, criteria.indexOf('*')).equals(criteria.substring(0, criteria.indexOf('*')))) {
                    StdOut.println("Findc: Found entry at \"" + curDir.getChildren().get(key).getPath() + "\".");
                }
            } else {
                if (key.substring(0, criteria.indexOf('*')).equals(criteria.substring(0, criteria.indexOf('*'))) && key.endsWith(criteria.substring(criteria.indexOf('*') + 1))) {
                    StdOut.println("Findc: Found entry at \"" + curDir.getChildren().get(key).getPath() + "\".");
                }
            }

            if (curDir.getChildren().get(key) instanceof INodeDirectory) {
                findc((INodeDirectory) curDir.getChildren().get(key), criteria);
            }
        }
    }

    @Override
    public void cycles() {

    }

    private INodeDirectory digger(String path) {
        if (path.charAt(0) != '/') {                        // All valid paths start with a slash
            StdOut.println("Error: Invalid path.");
            return null;
        }

        String[] names = path.split("/");                   // Return an array of all directory names
        lastDir = root;                                     // Always start at root
        targetName = names[names.length - 1];               // Last entry of names will be the file/dir to be created


        for (int i= 1; i < names.length - 1; i++) {         // Loop through for each but the first and last names
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
