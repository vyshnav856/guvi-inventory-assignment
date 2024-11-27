// Submitted by: Vyshnav R
// Email: vyshnavr856@gmail.com

import java.util.*;

// SortComparator is used to sort the items inside a particular category list in descending order
class SortComparator implements Comparator<Item> {
    @Override
    public int compare(Item item_1, Item item_2) {
        return Integer.compare(item_2.getQuantity(), item_1.getQuantity());
    }
}

// The Inventory class will contain the data structures and methods used to manage inventory
class Inventory {
    // Used to store the inventory, items are stored in the format <Category Name, ArrayList<Item>>
    HashMap<String, ArrayList<Item>> categoryInventory;
    //  Used to store the category of each item in the manner of <ID, Category Name>
    HashMap<String, String> itemCategory;
    // Used to store items where restock is needed
    ArrayList<Item> restock;
    // TreeMap used to store the item quantities in descending order as <Item Quantity, ArrayList<Item>>
    TreeMap<Integer, ArrayList<String>> topKItems;

    Inventory() {
        categoryInventory = new HashMap<>();
        itemCategory = new HashMap<>();
        restock = new ArrayList<>();
        topKItems = new TreeMap<>(Comparator.reverseOrder());
    }

    // Used to add items to the topK TreeMap
    public void addTopKItems(Item item) {
        String id = item.getId();
        int quantity = item.getQuantity();

        if (!topKItems.containsKey(quantity)) {
            topKItems.put(quantity, new ArrayList<>());
        }

        topKItems.get(quantity).add(id);
    }

    // Used to delete items from the topK TreeMap
    public void deleteTopKItems(Item item) {
        String id = item.getId();
        int quantity = item.getQuantity();

        if (!topKItems.containsKey(quantity)) {
            return;
        }

        ArrayList<String> quantityItems = topKItems.get(quantity);
        quantityItems.remove(id);
    }

    // Used to get an item from the inventory given the ID
    public Item getItem(String id) throws Exception {
        if (!itemCategory.containsKey(id)) {
            throw new Exception("Exception: Item with given ID does not exist in the inventory");
        }

        String category = itemCategory.get(id);
        Item item = null;
        for (Item i: categoryInventory.get(category)) {
            if (id.equals(i.id)) {
                item = i;
                break;
            }
        }

        return item;
    }

    // Used to add an item to the inventory
    public void addItem(String id, String name, String category, int quantity, int threshold) throws Exception {
        if (itemCategory.containsKey(id)) {
            throw new Exception("Exception: Item with same ID already exists");
        }

        if (!categoryInventory.containsKey(category)) {
            categoryInventory.put(category, new ArrayList<>());
        }

        Item newItem = new Item(id, name, category, quantity, threshold);
        categoryInventory.get(category).add(newItem);
        categoryInventory.get(category).sort(new SortComparator());
        itemCategory.put(id, category);
        addTopKItems(newItem);
    }

    public void updateItem(String id, int quantity, int threshold) throws Exception {
        if (!itemCategory.containsKey(id)) {
            throw new Exception("Exception: Item with this ID does not exist");
        }

        try {
            Item item = getItem(id);
            item.setQuantity(quantity);
            item.setThreshold(threshold);
        }

        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Used to get all the items inside a specific category
    public ArrayList<Item> getCategory(String category) throws Exception {
        if (!categoryInventory.containsKey(category)) {
            throw new Exception("Exception: The given category does not exist in the inventory");
        }

        return categoryInventory.get(category);
    }

    // Used to get items that need restocking
    public ArrayList<Item> getRestock() {
        restock.clear();
        for (String category: categoryInventory.keySet()) {
            for (Item i: categoryInventory.get(category)) {
                if (i.getQuantity() < i.getThreshold()) {
                    restock.add(i);
                }
            }
        }

        return restock;
    }

    // Used to delete an item from the inventory
    public void deleteItem(String id) throws Exception {
        if (!itemCategory.containsKey(id)) {
            throw new Exception("Exception: Item with given ID does not exist in the inventory");
        }

        try {
            String category = itemCategory.get(id);
            Item item = getItem(id);
            itemCategory.remove(id);
            ArrayList<Item> items = categoryInventory.get(category);
            items.removeIf(i -> i.getId().equals(id));
            deleteTopKItems(item);
        }

        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Used to get the top k items with the largest quantities
    public ArrayList<Item> getTopK(int k) {
        ArrayList<Item> topK = new ArrayList<>();
        int count = 0;
        for (Integer i: topKItems.keySet()) {
            ArrayList<String> current = topKItems.get(i);

            for (String id: current) {
                try {
                    topK.add(getItem(id));
                    count++;
                    if (count >= k) {
                        break;
                    }
                }

                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            if (count >= k) {
                break;
            }
        }

        return topK;
    }

    // Used to get the entire inventory HashMap
    public HashMap<String, ArrayList<Item>> getInventory() {
        return categoryInventory;
    }

    // Used to merge another inventory into the current inventory
    public void mergeInventory(Inventory newInventory) {
        HashMap<String, ArrayList<Item>> newCategoryInventory = newInventory.getInventory();
        for (String category: newCategoryInventory.keySet()) {
            for (Item i: newCategoryInventory.get(category)) {
                String newItemId = i.getId();
                if (itemCategory.containsKey(newItemId)) {
                    try {
                        Item oldItem = getItem(newItemId);
                        if (oldItem.getQuantity() < i.getQuantity()) {
                            deleteTopKItems(oldItem);
                            oldItem.setQuantity(i.getQuantity());
                            addTopKItems(oldItem);
                        }
                    }

                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }

                else {
                    try {
                        addItem(newItemId, i.getName(), i.getCategory(), i.getQuantity(), i.getThreshold());
                    }

                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }

}

public class Main {
    public static void printItems(ArrayList<Item> items) {
        System.out.println("ID\t\tName\t\tCategory\t\tQuantity\t\tThreshold");
        for (Item i: items) {
            System.out.print(i.getId() + "\t\t");
            System.out.print(i.getName() + "\t\t");
            System.out.print(i.getCategory() + "\t\t");
            System.out.print(i.getQuantity() + "\t\t");
            System.out.println(i.getThreshold());
        }
    }

    public static void runTests() {
        System.out.println("Running test suite");
        System.out.println("Creating Inventory object");
        Inventory inventory = new Inventory();

        int limit = 20;
        System.out.println("\nAdding " + limit + " Items to inventory");
        System.out.println("ID, Name, Category, Quantity, Threshold");
        String[] categories = {"Category 1", "Category 2", "Category 3", "Category 4", "Category 5"};
        Random random = new Random();
        for (int i = 0; i < limit; i++) {
            String id = String.valueOf(i);
            String name = "Item Name " + id;
            String category = categories[random.nextInt(categories.length)];
            int quantity = random.nextInt(90) + 10;
            int threshold = random.nextInt(90) + 10;

            try {
                inventory.addItem(id, name, category, quantity, threshold);
                System.out.println("Added: " + id + ", " + name + ", " + category + ", " + quantity + ", " + threshold);
            }

            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("\nUpdating item with ID 2");
        try {
            inventory.updateItem("2", 100, 50);
        }

        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Item after updating");
        try {
            Item updated = inventory.getItem("2");
            System.out.println("Updated quantity and threshold: " + updated.getQuantity() + " " + updated.getThreshold());
        }

        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\nDeleting item with ID 2");
        try {
            inventory.deleteItem("2");
        }

        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Trying to fetch deleted item, system will throw exception");
        try {
            Item deletedItem = inventory.getItem("2");
            System.out.println("Deleted item ID: " + deletedItem.getId());
        }

        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\nFetching all items in category 3");
        try {
            ArrayList<Item> items = inventory.getCategory("Category 3");
            printItems(items);
        }

        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\nGenerating restock notifications");
        ArrayList<Item> restock = inventory.getRestock();
        printItems(restock);

        System.out.println("\nCreating new Inventory object to test merging");
        Inventory inventory2 = new Inventory();
        System.out.println("Adding " + limit + " Items to new inventory");
        System.out.println("ID, Name, Category, Quantity, Threshold");
        for (int i = 0; i < limit; i++) {
            String id = String.valueOf(i + 100);
            String name = "Item Name " + id;
            String category = categories[random.nextInt(categories.length)];
            int quantity = random.nextInt(90) + 10;
            int threshold = random.nextInt(90) + 10;

            try {
                inventory2.addItem(id, name, category, quantity, threshold);
                System.out.println("Added: " + id + ", " + name + ", " + category + ", " + quantity + ", " + threshold);
            }

            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("\nMerging inventories");
        inventory.mergeInventory(inventory2);

        System.out.println("Inventory after merging, printing items of Category 1");
        try {
            ArrayList<Item> items = inventory.getCategory("Category 1");
            printItems(items);
        }

        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\nFetching top 5 items with highest quantity");
        ArrayList<Item> topItems = inventory.getTopK(5);
        printItems(topItems);
    }

    public static void main(String[] args) {
        runTests();
    }
}