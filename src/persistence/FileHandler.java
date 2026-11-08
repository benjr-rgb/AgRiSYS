package persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import exception.DataLoadException;
import managers.InventoryManager;
import objects.Beef;
import objects.Chicken;
import objects.Clerk;
import objects.Egg;
import objects.InventoryItem;
import objects.Manager;
import objects.Pork;
import objects.Product;
import objects.User;


public class FileHandler {

    // Relative paths: they work on every computer when you run from Eclipse,
    // as long as the "data" folder sits directly inside the project folder.
    public static final String DATA_FOLDER = "data";
    public static final String PRODUCTS_FILE = "data/products.txt";
    public static final String INVENTORY_FILE = "data/inventory.txt";
    public static final String USERS_FILE = "data/users.txt";

    // Number of fields per line, so we do not repeat "magic numbers" below.
    private static final int MEAT_FIELDS = 7;
    private static final int EGG_FIELDS = 9;

    // Messages about skipped lines. The screen/console can show them to the user.
    private ArrayList<String> warnings;

    public FileHandler() {
        warnings = new ArrayList<String>();
    }

    public ArrayList<String> getWarnings() { return warnings; }

    /** True if the file exists. */
    public boolean fileExists(String path) {
        return new File(path).exists();
    }

    // ------------------------------------------------------------------
    // PRODUCTS
    // ------------------------------------------------------------------

    public void saveProducts(ArrayList<Product> products) throws DataLoadException {
        ArrayList<String> lines = new ArrayList<String>();
        for (Product p : products) {            // "for each product p in the list"
            lines.add(p.toFileString());        // polymorphism: each class writes itself
        }
        writeLines(PRODUCTS_FILE, lines);
    }

    public ArrayList<Product> loadProducts() throws DataLoadException {
        ArrayList<Product> result = new ArrayList<Product>();
        ArrayList<String> lines = readLines(PRODUCTS_FILE);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;   // ignore blank lines
            }
            try {
                result.add(parseProduct(line));
            } catch (Exception e) {
                // A bad number, a missing field, an unknown category... skip this line.
                warnings.add("products.txt line " + (i + 1) + " skipped: " + e.getMessage());
            }
        }
        return result;
    }

    /** Turns ONE text line back into a Product object of the right class. */
    private Product parseProduct(String line) {
        // "-1" keeps empty fields at the end instead of dropping them.
        // "\\|" is needed because "|" has a special meaning in split().
        String[] parts = line.split("\\|", -1);
        String category = parts[0];

        // The expected number of fields depends on the category.
        int expected;
        if (category.equals("Egg")) {
            expected = EGG_FIELDS;
        } else if (category.equals("Pork") || category.equals("Beef") || category.equals("Chicken")) {
            expected = MEAT_FIELDS;
        } else {
            throw new IllegalArgumentException("unknown category \"" + category + "\"");
        }
        if (parts.length != expected) {
            throw new IllegalArgumentException("expected " + expected + " fields but found " + parts.length);
        }

        String id = parts[1];
        String name = parts[2];
        String description = parts[3];
        double price = Double.parseDouble(parts[4]);    // may throw NumberFormatException
        String unit = parts[5];
        int shelfLife = Integer.parseInt(parts[6]);

        if (category.equals("Pork")) {
            return new Pork(id, name, description, price, unit, shelfLife);
        } else if (category.equals("Beef")) {
            return new Beef(id, name, description, price, unit, shelfLife);
        } else if (category.equals("Chicken")) {
            return new Chicken(id, name, description, price, unit, shelfLife);
        } else {
            return new Egg(id, name, description, price, unit, shelfLife,
                    parts[7], Integer.parseInt(parts[8]));
        }
    }

    // ------------------------------------------------------------------
    // INVENTORY
    // ------------------------------------------------------------------

    public void saveInventory(InventoryManager inventory) throws DataLoadException {
        ArrayList<String> lines = new ArrayList<String>();
        for (InventoryItem item : inventory.getItems()) {
            lines.add(item.toFileString());
        }
        writeLines(INVENTORY_FILE, lines);
    }

    /**
     * Loads inventory lines. Each line only stores the product ID, so we need
     * the already-loaded product list to find the matching Product object.
     */
    public ArrayList<InventoryItem> loadInventory(ArrayList<Product> products)
            throws DataLoadException {
        ArrayList<InventoryItem> result = new ArrayList<InventoryItem>();
        ArrayList<String> lines = readLines(INVENTORY_FILE);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                String[] parts = line.split("\\|", -1);
                if (parts.length != 4) {
                    throw new IllegalArgumentException("expected 4 fields but found " + parts.length);
                }
                Product product = findProduct(products, parts[0]);
                if (product == null) {
                    throw new IllegalArgumentException("no product with ID " + parts[0]);
                }
                double quantity = Double.parseDouble(parts[1]);
                double reorderLevel = Double.parseDouble(parts[2]);
                LocalDate date = LocalDate.parse(parts[3]);   // format: 2026-11-04
                result.add(new InventoryItem(product, quantity, reorderLevel, date));
            } catch (Exception e) {
                warnings.add("inventory.txt line " + (i + 1) + " skipped: " + e.getMessage());
            }
        }
        return result;
    }

    private Product findProduct(ArrayList<Product> products, String id) {
        for (Product p : products) {
            if (p.getId().equalsIgnoreCase(id)) {
                return p;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------
    // USERS
    // ------------------------------------------------------------------

    public void saveUsers(ArrayList<User> users) throws DataLoadException {
        ArrayList<String> lines = new ArrayList<String>();
        for (User u : users) {
            lines.add(u.toFileString());
        }
        writeLines(USERS_FILE, lines);
    }

    public ArrayList<User> loadUsers() throws DataLoadException {
        ArrayList<User> result = new ArrayList<User>();
        ArrayList<String> lines = readLines(USERS_FILE);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                String[] parts = line.split("\\|", -1);
                if (parts.length != 3) {
                    throw new IllegalArgumentException("expected 3 fields but found " + parts.length);
                }
                if (parts[2].equals("Manager")) {
                    result.add(new Manager(parts[0], parts[1]));
                } else if (parts[2].equals("Clerk")) {
                    result.add(new Clerk(parts[0], parts[1]));
                } else {
                    throw new IllegalArgumentException("unknown role \"" + parts[2] + "\"");
                }
            } catch (Exception e) {
                warnings.add("users.txt line " + (i + 1) + " skipped: " + e.getMessage());
            }
        }
        return result;
    }

        // LOW-LEVEL HELPERS: read every line / write every line of a file
    
    /**
     * Reads all lines of a text file.
     * "try (...)" is "try-with-resources": Java closes the file for us
     * automatically, even if an error happens.
     */
    private ArrayList<String> readLines(String path) throws DataLoadException {
        ArrayList<String> lines = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new DataLoadException("Could not read " + path + ": " + e.getMessage());
        }
        return lines;
    }

    /** Writes the lines to a file (replacing the old content). */
    private void writeLines(String path, ArrayList<String> lines) throws DataLoadException {
        new File(DATA_FOLDER).mkdirs();   // make sure the "data" folder exists
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            for (String line : lines) {
                writer.println(line);
            }
        } catch (IOException e) {
            throw new DataLoadException("Could not save " + path + ": " + e.getMessage());
        }
    }
}