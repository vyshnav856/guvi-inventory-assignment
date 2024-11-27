// The Item class is used to store the details of a single item
public class Item {
    String id;
    String name;
    String category;
    int quantity;
    int threshold;

    Item(String id, String name, String category, int quantity, int threshold) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.threshold = threshold;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getThreshold() {
        return threshold;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }
}
