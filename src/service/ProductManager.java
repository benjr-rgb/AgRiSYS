package service;

import java.util.ArrayList;
import exception.DuplicateProductException;
import exception.ValidationException;
import model.Egg;
import model.InventoryItem;
import model.Product;

/**
 * ProductManager handles everything about the product list: add, edit,
 * delete, search, filter, AND all the validation rules.
 *
 * It works together with Inventory: adding a product also creates its stock
 * entry, and deleting a product also removes it.
 */
public class ProductManager {

    private ArrayList<Product> products;
    private Inventory inventory;   // the same Inventory object the system uses

    public ProductManager(Inventory inventory) {
        this.inventory = inventory;
        this.products = new ArrayList<Product>();
    }

    public ArrayList<Product> getProducts() { return products; }

    /** Replaces the whole list (used when loading from a file). */
    public void setProducts(ArrayList<Product> products) { this.products = products; }

    // ------------------------------------------------------------------
    // FINDING
    // ------------------------------------------------------------------

    /** Returns the product with this ID, or null if there is none. */
    public Product findById(String id) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equalsIgnoreCase(id.trim())) {
                return products.get(i);
            }
        }
        return null;
    }

    /** Returns the product with this exact name, or null if there is none. */
    public Product findByName(String name) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getName().equalsIgnoreCase(name.trim())) {
                return products.get(i);
            }
        }
        return null;
    }

    /** All products whose name contains the keyword (ignoring upper/lower case). */
    public ArrayList<Product> searchByName(String keyword) {
        ArrayList<Product> result = new ArrayList<Product>();
        String lowerKeyword = keyword.trim().toLowerCase();
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getName().toLowerCase().contains(lowerKeyword)) {
                result.add(products.get(i));
            }
        }
        return result;
    }

    /** All products of one category. */
    public ArrayList<Product> filterByCategory(String category) {
        ArrayList<Product> result = new ArrayList<Product>();
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getCategory().equalsIgnoreCase(category)) {
                result.add(products.get(i));
            }
        }
        return result;
    }

    // ------------------------------------------------------------------
    // ADD
    // ------------------------------------------------------------------

    /**
     * Adds a new product AND creates its inventory entry.
     * Throws ValidationException (or DuplicateProductException) if a rule is broken.
     */
    public void addProduct(Product p, double initialQuantity, double reorderLevel)
            throws ValidationException {

        // Remove accidental spaces first, so "  Pork Belly " equals "Pork Belly".
        p.setId(p.getId().trim());
        p.setName(p.getName().trim());
        p.setDescription(p.getDescription().trim());
        p.setUnit(p.getUnit().trim().toLowerCase());

        validateProduct(p);

        if (initialQuantity < 0) {
            throw new ValidationException("Initial quantity cannot be negative.");
        }
        if (reorderLevel < 0) {
            throw new ValidationException("Reorder level cannot be negative.");
        }
        if (findById(p.getId()) != null) {
            throw new DuplicateProductException("A product with ID " + p.getId() + " already exists.");
        }
        if (findByName(p.getName()) != null) {
            throw new DuplicateProductException("A product named \"" + p.getName() + "\" already exists.");
        }

        products.add(p);
        inventory.addItem(new InventoryItem(p, initialQuantity, reorderLevel));
    }

    // ------------------------------------------------------------------
    // EDIT
    // ------------------------------------------------------------------

    /** Edits the fields every product has (name, description, price, unit, shelf life). */
    public void editProduct(String id, String name, String description,
                            double price, String unit, int shelfLifeDays)
            throws ValidationException {

        Product p = findById(id);
        if (p == null) {
            throw new ValidationException("Product ID " + id + " was not found.");
        }

        name = name.trim();
        description = description.trim();
        unit = unit.trim().toLowerCase();

        // Check everything BEFORE changing anything, so a bad edit changes nothing.
        validateFields(p.getId(), name, description, price, unit, shelfLifeDays);

        // The new name must not belong to a DIFFERENT product.
        Product sameName = findByName(name);
        if (sameName != null && sameName != p) {
            throw new DuplicateProductException("Another product is already named \"" + name + "\".");
        }

        p.setName(name);
        p.setDescription(description);
        p.setPricePerUnit(price);
        p.setUnit(unit);
        p.setShelfLifeDays(shelfLifeDays);
    }

    /** Edits the extra fields of an Egg product. */
    public void editEggDetails(String id, String size, int piecesPerTray) throws ValidationException {
        Product p = findById(id);
        if (!(p instanceof Egg)) {
            throw new ValidationException("Product " + id + " is not an egg product.");
        }
        if (isBlank(size)) {
            throw new ValidationException("Egg size cannot be empty.");
        }
        if (piecesPerTray <= 0) {
            throw new ValidationException("Pieces per tray must be greater than 0.");
        }
        Egg egg = (Egg) p;
        egg.setSize(size.trim());
        egg.setPiecesPerTray(piecesPerTray);
    }

    // ------------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------------

    /** Deletes a product AND its inventory entry. */
    public void deleteProduct(String id) throws ValidationException {
        Product p = findById(id);
        if (p == null) {
            throw new ValidationException("Product ID " + id + " was not found.");
        }
        products.remove(p);
        inventory.removeItem(p.getId());
    }

    // ------------------------------------------------------------------
    // VALIDATION (private helpers, only used inside this class)
    // ------------------------------------------------------------------

    /** Validates a whole product object, including its category-specific fields. */
    private void validateProduct(Product p) throws ValidationException {
        validateFields(p.getId(), p.getName(), p.getDescription(),
                p.getPricePerUnit(), p.getUnit(), p.getShelfLifeDays());

        if (!Product.isInList(p.getCategory(), Product.CATEGORIES)) {
            throw new ValidationException("Category must be one of: Pork, Beef, Chicken, Egg.");
        }
        
        if (p instanceof Egg) {
            Egg egg = (Egg) p;
            if (isBlank(egg.getSize())) {
                throw new ValidationException("Egg size cannot be empty.");
            }
            if (egg.getPiecesPerTray() <= 0) {
                throw new ValidationException("Pieces per tray must be greater than 0.");
            }
        }
    }

    /** Validates the fields that every product has. */
    private void validateFields(String id, String name, String description,
                                double price, String unit, int shelfLifeDays)
            throws ValidationException {
        if (isBlank(id)) {
            throw new ValidationException("Product ID cannot be empty.");
        }
        if (isBlank(name)) {
            throw new ValidationException("Name cannot be empty.");
        }
        if (isBlank(description)) {
            throw new ValidationException("Description cannot be empty.");
        }
        if (price <= 0) {
            throw new ValidationException("Price must be greater than 0.");
        }
        if (!Product.isInList(unit, Product.UNITS)) {
            throw new ValidationException("Unit must be one of: kg, pieces.");
        }
        if (shelfLifeDays <= 0) {
            throw new ValidationException("Shelf life must be at least 1 day.");
        }
    }

    /** True if the text is null, empty, or only spaces. */
    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
}
