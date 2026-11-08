package app;

import java.util.ArrayList;
import exception.DataLoadException;
import exception.ValidationException;
import model.InventoryItem;
import model.Product;
import model.User;
import persistence.CsvImporter;
import persistence.FileHandler;
import service.AuthService;
import service.Inventory;
import service.ProductManager;

public class PorkingLotSystem {

    // Used on the first run when products come from the CSV file.
    public static final double DEFAULT_QUANTITY = 50;
    public static final double DEFAULT_REORDER_LEVEL = 10;

    private FileHandler fileHandler;
    private CsvImporter csvImporter;
    private Inventory inventory;
    private ProductManager productManager;
    private AuthService authService;
    private User currentUser;   // null when nobody is logged in

    public PorkingLotSystem() {
        fileHandler = new FileHandler();
        csvImporter = new CsvImporter();
        inventory = new Inventory();
        productManager = new ProductManager(inventory);   // shares the same Inventory
        authService = new AuthService();
        currentUser = null;
    }


    /**
     * Loads everything. Call this ONCE when the program starts.
     * Returns a list of messages (info and warnings) for the screen to show.
     */
    public ArrayList<String> startUp() {
        ArrayList<String> messages = new ArrayList<String>();

        // ---------- 1. USERS ----------
        try {
            if (fileHandler.fileExists(FileHandler.USERS_FILE)) {
                authService.setUsers(fileHandler.loadUsers());
            }
            // First run (no file) or file with no valid users: create the defaults.
            if (authService.getUsers().isEmpty()) {
                authService.seedDefaultUsers();
                messages.add("Default accounts created: manager/manager123 and clerk/clerk123");
            }
        } catch (DataLoadException e) {
            messages.add(e.getMessage());
            authService.seedDefaultUsers();
        }

        
        try {
            if (fileHandler.fileExists(FileHandler.PRODUCTS_FILE)) {
                // Normal run: load the saved files.
                ArrayList<Product> loaded = fileHandler.loadProducts();
                productManager.setProducts(loaded);

                if (fileHandler.fileExists(FileHandler.INVENTORY_FILE)) {
                    inventory.setItems(fileHandler.loadInventory(loaded));
                }
                // Safety net: every product must have a stock entry.
                for (Product p : loaded) {
                    if (inventory.findByProductId(p.getId()) == null) {
                        inventory.addItem(new InventoryItem(p, DEFAULT_QUANTITY, DEFAULT_REORDER_LEVEL));
                    }
                }
            } else if (fileHandler.fileExists(FileHandler.CSV_FILE)) {
                // First run: seed from the CSV file.
                ArrayList<Product> imported = csvImporter.importProducts(FileHandler.CSV_FILE);
                for (Product p : imported) {
                    try {
                        // addProduct validates AND creates the inventory entry.
                        productManager.addProduct(p, DEFAULT_QUANTITY, DEFAULT_REORDER_LEVEL);
                    } catch (ValidationException e) {
                        messages.add("Skipped " + p.getName() + ": " + e.getMessage());
                    }
                }
                messages.add("First run: imported " + productManager.getProducts().size()
                        + " products from " + FileHandler.CSV_FILE);
            } else {
                messages.add("No product data found. Starting with an empty product list.");
            }
        } catch (DataLoadException e) {
            messages.add(e.getMessage());
        }

        messages.addAll(csvImporter.getWarnings());
        messages.addAll(fileHandler.getWarnings());

        try {
            saveAll();
        } catch (DataLoadException e) {
            messages.add(e.getMessage());
        }
        return messages;
    }

    /** Saves products, inventory and users to their files. */
    public void saveAll() throws DataLoadException {
        fileHandler.saveProducts(productManager.getProducts());
        fileHandler.saveInventory(inventory);
        fileHandler.saveUsers(authService.getUsers());
    }

    // LOGIN / LOGOUT

    /** Returns the logged-in User, or null if the login failed. */
    public User login(String username, String password) {
        currentUser = authService.login(username, password);
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // PERMISSION CHECKS (private helpers)

    private void requireEditRights() throws ValidationException {
        if (currentUser == null) {
            throw new ValidationException("Please log in first.");
        }
        if (!currentUser.canEditProducts()) {
            throw new ValidationException("Access denied: only a Manager can add, edit or delete products.");
        }
    }

    private void requireRestockRights() throws ValidationException {
        if (currentUser == null) {
            throw new ValidationException("Please log in first.");
        }
        if (!currentUser.canRestock()) {
            throw new ValidationException("Access denied: only a Manager can restock or change reorder levels.");
        }
    }

    // PRODUCT ACTIONS Manager only, each one saves afterwards
    
    public void addProduct(Product p, double initialQuantity, double reorderLevel)
            throws ValidationException, DataLoadException {
        requireEditRights();
        productManager.addProduct(p, initialQuantity, reorderLevel);
        saveAll();
    }

    public void editProduct(String id, String name, String description,
                            double price, String unit, int shelfLifeDays)
            throws ValidationException, DataLoadException {
        requireEditRights();
        productManager.editProduct(id, name, description, price, unit, shelfLifeDays);
        saveAll();
    }

    public void editEggDetails(String id, String size, int piecesPerTray)
            throws ValidationException, DataLoadException {
        requireEditRights();
        productManager.editEggDetails(id, size, piecesPerTray);
        saveAll();
    }

    public void deleteProduct(String id) throws ValidationException, DataLoadException {
        requireEditRights();
        productManager.deleteProduct(id);
        saveAll();
    }

    // INVENTORY ACTIONS (Manager only, each one saves afterwards)
    
    public void restock(String productId, double amount)
            throws ValidationException, DataLoadException {
        requireRestockRights();
        inventory.restock(productId, amount);
        saveAll();
    }

    public void setReorderLevel(String productId, double level)
            throws ValidationException, DataLoadException {
        requireRestockRights();
        inventory.setReorderLevel(productId, level);
        saveAll();
    }

    // VIEWING (everyone can do these, so no permission check)
    
    public ArrayList<Product> getAllProducts() { return productManager.getProducts(); }

    public Product findProduct(String id) { return productManager.findById(id); }

    public ArrayList<Product> searchProducts(String keyword) {
        return productManager.searchByName(keyword);
    }

    public ArrayList<Product> filterProducts(String category) {
        return productManager.filterByCategory(category);
    }

    public ArrayList<InventoryItem> getInventoryItems() { return inventory.getItems(); }

    public ArrayList<InventoryItem> filterInventory(String category) {
        return inventory.filterByCategory(category);
    }

    public ArrayList<InventoryItem> getLowStockItems() { return inventory.getLowStockItems(); }

    public ArrayList<InventoryItem> getExpiringSoon(int days) {
        return inventory.getExpiringSoon(days);
    }
}
