/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 */
public abstract class INode {

    private String name;

    private long accessTime;

    private String path;

    public INode(String name, String path) {
      this.name = name;
      this.accessTime = System.currentTimeMillis();
        this.path = path;
    }

    public long getAccessTime() {
      return accessTime;
    }

    public void setAccessTime(long accessTime) {
      this.accessTime = accessTime;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getPath() {
        return path;
    }
}
