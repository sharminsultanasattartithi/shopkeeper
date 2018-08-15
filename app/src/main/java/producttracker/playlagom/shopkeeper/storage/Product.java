package producttracker.playlagom.shopkeeper.storage;

import java.io.Serializable;

/**
 * Created by User on 8/9/2018.
 */

public class Product implements Serializable{

    String name;
    String url;
    private String nodeKey;

    public Product() {

    }

    public Product(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getNodeKey() {
        return nodeKey;
    }
}
