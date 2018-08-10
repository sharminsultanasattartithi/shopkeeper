package producttracker.playlagom.shopkeeper.storage;

/**
 * Created by User on 8/9/2018.
 */

public class Product {

    String name;
    String url;

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
}
