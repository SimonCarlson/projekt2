/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 */
public interface FileSystem {

    public void mkdir(String path);
  
    public void touch(String path);

    public void ln(String source, String target);

    public void find(String name);

    public void findc(String criteria);

    public void cycles();
}
