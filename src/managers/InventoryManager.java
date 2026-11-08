package managers;

import java.util.ArrayList;
import exception.ValidationException;
import objects.InventoryItem;

/**
 * Inventory is the "stock book": it holds ALL InventoryItems and offers
 * methods to find, restock, filter and remove them.
 *
 * Important rule for this package: NO Scanner and NO System.out.println here.
 * These classes only take values, return values, or throw exceptions.
 * That is what lets us plug a JavaFX screen on top later without rewriting.
 */
public class InventoryManager {

    private ArrayList<InventoryItem> items;

    public InventoryManager() {
        items = new ArrayList<InventoryItem>();
    }

    public ArrayList<InventoryItem> getItems() { return items; }

    /** Replaces the whole list (used when loading from a file). */
    public void setItems(ArrayList<InventoryItem> items) { this.items = items; }

    public void addItem(InventoryItem item) {
        items.add(item);
    }

    /** Finds the stock entry of a product. Returns null if there is none. */
    public InventoryItem findByProductId(String productId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getProduct().getId().equalsIgnoreCase(productId)) {
                return items.get(i);
            }
        }
        return null;
    }

    /** Adds stock to a product. */
    public void restock(String productId, double amount) throws ValidationException {
        InventoryItem item = findByProductId(productId);
        if (item == null) {
            throw new ValidationException("No inventory entry for product ID " + productId + ".");
        }
        item.restock(amount); // the item itself checks that amount > 0
    }

    /** Changes the low-stock threshold of a product. */
    public void setReorderLevel(String productId, double level) throws ValidationException {
        InventoryItem item = findByProductId(productId);
        if (item == null) {
            throw new ValidationException("No inventory entry for product ID " + productId + ".");
        }
        item.setReorderLevel(level);
    }

    /** Removes the stock entry of a product (used when the product is deleted). */
    public void removeItem(String productId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getProduct().getId().equalsIgnoreCase(productId)) {
                items.remove(i);
                return; // we found it, so we can stop looping
            }
        }
    }

    /** All items whose quantity is at or below their reorder level. */
    public ArrayList<InventoryItem> getLowStockItems() {
        ArrayList<InventoryItem> result = new ArrayList<InventoryItem>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isLowStock()) {
                result.add(items.get(i));
            }
        }
        return result;
    }

    /** All items that expire within "days" days (already expired ones included). */
    public ArrayList<InventoryItem> getExpiringSoon(int days) {
        ArrayList<InventoryItem> result = new ArrayList<InventoryItem>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isExpiringSoon(days)) {
                result.add(items.get(i));
            }
        }
        return result;
    }

    /** All items whose product belongs to the given category ("Pork", "Egg", ...). */
    public ArrayList<InventoryItem> filterByCategory(String category) {
        ArrayList<InventoryItem> result = new ArrayList<InventoryItem>();
        for (int i = 0; i < items.size(); i++) {
            // getCategory() is POLYMORPHIC: each product class answers for itself
            if (items.get(i).getProduct().getCategory().equalsIgnoreCase(category)) {
                result.add(items.get(i));
            }
        }
        return result;
    }
}
